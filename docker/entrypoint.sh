#!/bin/bash

show_progress() {
    message="$1"; percent="$2"
    done=$(bc <<< "scale=0; 40 * ${percent%.*} / 100" )
    todo=$(bc <<< "scale=0; 40 - $done" )
    done_sub_bar=$(printf "%${done}s" | tr " " "#")
    todo_sub_bar=$(printf "%${todo}s" | tr " " "-")
    echo -e "$message [${done_sub_bar}${todo_sub_bar}] ${percent}%"
}

node_verification() {
    echo "Stating Cardano node verification..."

    REGEX_VALIDATED="^.?\[.*\].?\[.*\].?(Validating|Validated) chunk no\. ([0-9]+) out of ([0-9]+)\. Progress: ([0-9]+\.[0-9][0-9])\%.*$"
    REGEX_REPLAYED="^.?\[.*\].?\[.*\].?Replayed block: slot ([0-9]+) out of ([0-9]+)\. Progress: ([0-9]+\.[0-9][0-9])\%.*$"
    REGEX_PUSHING="^.?\[.*\].?\[.*\].?Pushing ledger state for block ([a-f0-9]+) at slot ([0-9]+)\. Progress: ([0-9]+\.[0-9][0-9])\%.*$"
    REGEX_STARTED="^.?\[.*\].?\[.*\].?(Started .*)$"

    while [ ! -S "$CARDANO_NODE_SOCKET_PATH" ]; do
        new_line=$(tail -n 1 /logs/node.log)
        if [ "${new_line}" == "${line}" ]; then continue; fi
        line=$new_line
        if [[ "$line" =~ $REGEX_VALIDATED ]]; then
            show_progress "Node verification: Chunk ${BASH_REMATCH[2]}/${BASH_REMATCH[3]}" ${BASH_REMATCH[4]}
        elif [[ "$line" =~ $REGEX_REPLAYED ]]; then
            show_progress "Replayed block: Block ${BASH_REMATCH[1]}/${BASH_REMATCH[2]}"  ${BASH_REMATCH[3]}
        elif [[ "$line" =~ $REGEX_PUSHING ]]; then
            show_progress "Pushing ledger state: Slot ${BASH_REMATCH[2]}" ${BASH_REMATCH[3]}
        elif [[ "$line" =~ $REGEX_STARTED ]]; then
            echo -e "${BASH_REMATCH[1]}..."
        fi
        sleep 1
    done
    echo "Node verification: DONE"
}

node_synchronization() {
    echo -e "Starting Cardano node synchronization..."
    epoch_length=$(jq -r .epochLength $GENESIS_SHELLEY_PATH)
    slot_length=$(jq -r .slotLength $GENESIS_SHELLEY_PATH)
    byron_slot_length=$(( $(jq -r .blockVersionData.slotDuration $GENESIS_BYRON_PATH) / 1000 ))
    byron_epoch_length=$(( $(jq -r .protocolConsts.k $GENESIS_BYRON_PATH) * 10 ))
    byron_start=$(jq -r .startTime $GENESIS_BYRON_PATH)
    byron_end=$((byron_start + $HARDFORK_EPOCH * byron_epoch_length * byron_slot_length))
    byron_slots=$(($HARDFORK_EPOCH * byron_epoch_length))
    now=$(date +'%s')
    expected_slot=$((byron_slots + (now - byron_end) / slot_length))

    sync_progress=0
    while (( ${sync_progress%.*} < 100 )); do
        current_status=$(cardano-cli query tip $NETWORK_STR)
        current_slot=$(echo $current_status | jq -r '.slot')
        sync_progress=$(echo $current_status | jq -r '.syncProgress')

        show_progress "Node synchronization: Slot $current_slot/$expected_slot" $sync_progress
        sleep 1
    done
    echo "Node synchronization: DONE"
}

database_initialization() {
    echo "Starting database initialization..."
    echo "postgres" >> /tmp/password
    initdb_command="/usr/lib/postgresql/$PG_VERSION/bin/initdb --pgdata=/node/postgres --auth=md5 --auth-local=md5 --auth-host=md5 --username=postgres --pwfile=/tmp/password"
    sudo -H -u postgres bash -c "$initdb_command"
}

create_database_and_user() {
    export DB_SCHEMA=$NETWORK

    flag=true
    while [ $(sudo -u postgres psql -U postgres -Atc "SELECT pg_is_in_recovery()";) == "t" ]; do
        if $flag ; then
            echo "Waiting for database recovery..."
            flag=false
        fi
        sleep 1
    done

    if [[ -z $(sudo -u postgres psql -U postgres -Atc "SELECT 1 FROM pg_catalog.pg_user WHERE usename = '$DB_USER'";) ]]; then
      echo "Creating database..."
      sudo -u postgres psql -U postgres -c "CREATE ROLE \"$DB_USER\" with LOGIN CREATEDB PASSWORD '$DB_SECRET';" > /dev/null
    fi

    if [[ -z $(sudo -u postgres psql -U postgres -Atc "SELECT 1 FROM pg_catalog.pg_database WHERE datname = '$DB_NAME'";) ]]; then
      echo "Creating user..."
      sudo -u postgres psql -U postgres -c "CREATE DATABASE \"$DB_NAME\";" >/dev/null
      sudo -u postgres psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE \"$DB_NAME\" to \"$DB_USER\";" > /dev/null
    fi
}

get_current_index() {
    json="{\"network_identifier\":{\"blockchain\":\"cardano\",\"network\":\"${NETWORK}\"},\"metadata\":{}}"
    response=$(curl -s -X POST -H "Content-Type: application/json" -H "Content-length: 1000" -H "Host: localhost.com" --data "$json" "localhost:{$API_PORT}/network/status")
    current_index=$(echo $response | jq -r '.current_block_identifier.index')
    if [[ -z "$current_index" || "$current_index" == "null" ]]; then current_index=0; fi
}

echo "Network: $NETWORK"
if [ "$NETWORK" == "mainnet" ]; then
    NETWORK_STR="--mainnet"
    HARDFORK_EPOCH=208
else
    NETWORK_STR="--testnet-magic $PROTOCOL_MAGIC"
    HARDFORK_EPOCH=1
fi

echo "Starting Cardano node..."
cp -r /networks/$NETWORK/* /config/
rm -f $CARDANO_NODE_SOCKET_PATH
mkdir -p "$(dirname "$CARDANO_NODE_SOCKET_PATH")"
sleep 1
cardano-node run --socket-path "$CARDANO_NODE_SOCKET_PATH" --port $CARDANO_NODE_PORT --database-path /node/db --config /config/config.json --topology /config/topology.json > /logs/node.log &
sleep 2

if [ "${VERIFICATION}" == "true" ] || [ "${SYNC}" == "true" ] ; then
    node_verification
fi

if [ "${SYNC}" == "true" ] ; then
    node_synchronization
fi

echo "Starting Cardano submit api..."
cardano-submit-api --socket-path "$CARDANO_NODE_SOCKET_PATH" --port $NODE_SUBMIT_API_PORT $NETWORK_STR  --config /cardano-submit-api-config/cardano-submit-api.yaml > /logs/submit-api.log &

chown -R postgres:postgres /node/postgres
chmod -R 0700 /node/postgres
if [ ! -f "/node/postgres/PG_VERSION" ]; then
    database_initialization
fi

echo "Starting Postgres..."
/etc/init.d/postgresql start
create_database_and_user

echo "Starting Yaci indexer..."
exec java -jar /yaci-indexer/app.jar > /logs/indexer.log &

echo "Starting Rosetta API..."
exec java -jar /api/app.jar > /logs/api.log &

echo "Waiting Rosetta API initialization..."
sleep 5
get_current_index
while (( ! $current_index > 0 )); do
    get_current_index
    sleep 2
done

echo "DONE"

$@
