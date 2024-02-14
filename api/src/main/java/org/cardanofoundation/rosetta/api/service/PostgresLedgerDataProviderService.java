package org.cardanofoundation.rosetta.api.service;

import static org.cardanofoundation.rosetta.api.mapper.DataMapper.mapToTransactionPoolRegistrations;
import static org.cardanofoundation.rosetta.api.mapper.DataMapper.mapTransactionsToDict;
import static org.cardanofoundation.rosetta.api.mapper.DataMapper.parseTransactionRows;
import static org.cardanofoundation.rosetta.api.mapper.DataMapper.populateTransactionField;

import jakarta.annotation.PostConstruct;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.cardanofoundation.rosetta.api.config.RosettaConfig;
import org.cardanofoundation.rosetta.api.exception.ExceptionFactory;
import org.cardanofoundation.rosetta.api.mapper.DataMapper;
import org.cardanofoundation.rosetta.api.model.ProtocolParameters;
import org.cardanofoundation.rosetta.api.model.rest.BlockIdentifier;
import org.cardanofoundation.rosetta.api.model.rest.Currency;
import org.cardanofoundation.rosetta.api.model.rest.MaBalance;
import org.cardanofoundation.rosetta.api.model.rest.TransactionDto;
import org.cardanofoundation.rosetta.api.model.rest.Utxo;
import org.cardanofoundation.rosetta.api.projection.BlockProjection;
import org.cardanofoundation.rosetta.api.projection.EpochParamProjection;
import org.cardanofoundation.rosetta.api.projection.FindTransactionProjection;
import org.cardanofoundation.rosetta.api.projection.GenesisBlockProjection;
import org.cardanofoundation.rosetta.api.projection.dto.BlockDto;
import org.cardanofoundation.rosetta.api.projection.dto.FindPoolRetirements;
import org.cardanofoundation.rosetta.api.projection.dto.FindTransactionDelegations;
import org.cardanofoundation.rosetta.api.projection.dto.FindTransactionDeregistrations;
import org.cardanofoundation.rosetta.api.projection.dto.FindTransactionPoolOwners;
import org.cardanofoundation.rosetta.api.projection.dto.FindTransactionPoolRegistrationsData;
import org.cardanofoundation.rosetta.api.projection.dto.FindTransactionPoolRelays;
import org.cardanofoundation.rosetta.api.projection.dto.FindTransactionRegistrations;
import org.cardanofoundation.rosetta.api.projection.dto.FindTransactionWithdrawals;
import org.cardanofoundation.rosetta.api.projection.dto.FindTransactionsInputs;
import org.cardanofoundation.rosetta.api.projection.dto.FindTransactionsOutputs;
import org.cardanofoundation.rosetta.api.projection.dto.GenesisBlockDto;
import org.cardanofoundation.rosetta.api.projection.dto.PopulatedTransaction;
import org.cardanofoundation.rosetta.api.projection.dto.TransactionMetadataDto;
import org.cardanofoundation.rosetta.api.projection.dto.TransactionPoolRegistrations;
import org.cardanofoundation.rosetta.api.repository.BlockRepository;
import org.cardanofoundation.rosetta.api.repository.DelegationRepository;
import org.cardanofoundation.rosetta.api.repository.EpochParamRepository;
import org.cardanofoundation.rosetta.api.repository.PoolRetireRepository;
import org.cardanofoundation.rosetta.api.repository.PoolUpdateRepository;
import org.cardanofoundation.rosetta.api.repository.RewardRepository;
import org.cardanofoundation.rosetta.api.repository.StakeDeregistrationRepository;
import org.cardanofoundation.rosetta.api.repository.TxMetadataRepository;
import org.cardanofoundation.rosetta.api.repository.TxRepository;
import org.cardanofoundation.rosetta.api.repository.customrepository.UtxoRepository;
import org.cardanofoundation.rosetta.common.entity.Block;
import org.cardanofoundation.rosetta.common.entity.Tx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostgresLedgerDataProviderService implements LedgerDataProviderService {

  private final Map<String, PostgresLedgerDataProviderClient> clients = new HashMap<>();
  private final RosettaConfig rosettaConfig;
  private final BlockRepository blockRepository;
  private final RewardRepository rewardRepository;
  private final UtxoRepository utxoRepository;
  private final TxRepository txRepository;
  private final EpochParamRepository epochParamRepository;
  private final StakeDeregistrationRepository stakeDeregistrationRepository;
  private final DelegationRepository delegationRepository;
  private final TxMetadataRepository txMetadataRepository;
  private final PoolUpdateRepository poolUpdateRepository;
  private final PoolRetireRepository poolRetireRepository;

  @PostConstruct
  void init() {
    rosettaConfig.getNetworks().forEach(networkConfig -> {
      clients.put(networkConfig.getSanitizedNetworkId(), PostgresLedgerDataProviderClient.builder()
          .networkId(networkConfig.getSanitizedNetworkId()).build());
    });
  }

  @Override
  public BlockIdentifier getTip(final String networkId) {
    if (clients.containsKey(networkId)) {
      return clients.get(networkId).getTip();
    }

    throw new IllegalArgumentException("Invalid network id specified.");
  }

  @Override
  public GenesisBlockDto findGenesisBlock() {
    log.debug("[findGenesisBlock] About to run findGenesisBlock query");
    List<Block> blocks = blockRepository.findGenesisBlock();
    if(!blocks.isEmpty()) {
      Block genesis = blocks.get(0);
      return GenesisBlockDto.builder().hash(genesis.getHash())
          .number(genesis.getNumber())
          .build();
    }
    log.debug("[findGenesisBlock] Genesis block was not found");
    return null;
  }

  @Override
  public BlockDto findBlock(Long blockNumber, String blockHash) {
    log.debug(
        "[findBlock] Parameters received for run query blockNumber: {} , blockHash: {}",
        blockNumber, blockHash);
    List<Block> blocks;
    if(blockHash == null && blockNumber != null) {
      blocks = blockRepository.findByNumber(blockNumber);
    } else if(blockHash != null && blockNumber == null){
      blocks = blockRepository.findByHash(blockHash);
    } else {
      blocks = blockRepository.findByNumberAndHash(blockNumber, blockHash);
    }
    if (!blocks.isEmpty()) {
      log.debug("[findBlock] Block found!");
      Block block = blocks.get(0);
      try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = dateFormat.parse(block.getCreateDatetime().toString());
        return BlockDto.fromBlock(block);
      } catch (ParseException e) {
        log.error(e.getMessage());
      }

    }
    log.debug("[findBlock] No block was found");
    return null;
  }

  @Override
  public Long findBalanceByAddressAndBlock(String address, String hash) {
    return rewardRepository.findRewardBalanceByAddressAndBlock(address, hash)
        - rewardRepository.findWithdrwalBalanceByAddressAndBlock(address, hash);
  }

  @Override
  public Long findLatestBlockNumber() {
        return blockRepository.findLatestBlockNumber();
  }

  @Override
  public ProtocolParameters findProtocolParameters() {
    log.debug("[findProtocolParameters] About to run findProtocolParameters query");
    Page<EpochParamProjection> epochParamProjectionPage =
        epochParamRepository.findProtocolParameters(PageRequest.of(0, 1));
    if (ObjectUtils.isEmpty(epochParamProjectionPage.getContent())) {
      return ProtocolParameters.builder()
          .coinsPerUtxoSize("0")
          .maxValSize(BigInteger.ZERO)
          .maxCollateralInputs(0)
          .build();
    }
    EpochParamProjection epochParamProjection = epochParamProjectionPage.getContent().get(0);
    log.debug(
        "[findProtocolParameters] epochParamProjection is " + epochParamProjection.toString());
    return ProtocolParameters.builder()
        .coinsPerUtxoSize(
            Objects.nonNull(epochParamProjection.getCoinsPerUtxoSize()) ?
                epochParamProjection.getCoinsPerUtxoSize().toString() : "0")
        .maxTxSize(epochParamProjection.getMaxTxSize())
        .maxValSize(Objects.nonNull(epochParamProjection.getMaxValSize()) ?
            epochParamProjection.getMaxValSize() : BigInteger.ZERO)
        .keyDeposit(Objects.nonNull(epochParamProjection.getKeyDeposit()) ?
            epochParamProjection.getKeyDeposit().toString() : null)
        .maxCollateralInputs(epochParamProjection.getMaxCollateralInputs())
        .minFeeCoefficient(epochParamProjection.getMinFeeA())
        .minFeeConstant(epochParamProjection.getMinFeeB())
        .minPoolCost(Objects.nonNull(epochParamProjection.getMinPoolCost()) ?
            epochParamProjection.getMinPoolCost().toString() : null)
        .poolDeposit(Objects.nonNull(epochParamProjection.getPoolDeposit()) ?
            epochParamProjection.getPoolDeposit().toString() : null)
        .protocol(epochParamProjection.getProtocolMajor())
        .build();
  }

  @Override
  public List<Utxo> findUtxoByAddressAndBlock(String address, String hash,
      List<Currency> currencies) {

    return utxoRepository.findUtxoByAddressAndBlock(address, hash, currencies);
  }

  @Override
  public List<MaBalance> findMaBalanceByAddressAndBlock(String address, String hash) {
    return utxoRepository.findMaBalanceByAddressAndBlock(address, hash);
  }

  @Override
  public BlockDto findLatestBlock() {
    log.info("[getLatestBlock] About to look for latest block");
    Long latestBlockNumber = findLatestBlockNumber();
    log.info("[getLatestBlock] Latest block number is {}", latestBlockNumber);
    BlockDto latestBlock = findBlock(latestBlockNumber, null);
    if (Objects.isNull(latestBlock)) {
      log.error("[getLatestBlock] Latest block not found");
      throw ExceptionFactory.blockNotFoundException();
    }
    log.debug("[getLatestBlock] Returning latest block {}", latestBlock);
    return latestBlock;
  }

  @Override
  public List<TransactionDto> findTransactionsByBlock(Long blockNumber, String blockHash) {
    log.debug(
        "[findTransactionsByBlock] Parameters received for run query blockNumber: {} blockHash: {}",
        blockNumber, blockHash);

    List<Block> byNumberAndHash = blockRepository.findByNumberAndHash(blockNumber, blockHash);
    if(byNumberAndHash.isEmpty()) {
      log.debug(
          "[findTransactionsByBlock] No block found for blockNumber: {} blockHash: {}",
          blockNumber, blockHash);
      return null;
    }
    List<Tx> txList = byNumberAndHash.get(0).getTxList();
    log.debug(
        "[findTransactionsByBlock] Found {} transactions", txList.size());
    if (ObjectUtils.isNotEmpty(txList)) {
      return parseTransactionRows(txList);
    }
    return null;
  }

  @Override
  public List<PopulatedTransaction> fillTransaction(List<TransactionDto> transactions) {
    if (ObjectUtils.isNotEmpty(transactions)) {
      Map<String, PopulatedTransaction> transactionMap = mapTransactionsToDict(transactions);
      return populateTransactions(transactionMap);
    }
    log.debug(
        "[fillTransaction] Since no transactions were given, no inputs and outputs are looked for");
    return null;
  }

  @Override
  public List<PopulatedTransaction> populateTransactions(
      Map<String, PopulatedTransaction> transactionsMap) {

    List<String> transactionsHashes = transactionsMap.keySet().stream().toList();

    List<FindTransactionsInputs> inputs = getFindTransactionsInputs(
        transactionsHashes);
    List<FindTransactionsOutputs> outputs = getFindTransactionsOutputs(
        transactionsHashes);
    List<FindTransactionWithdrawals> withdrawals = getFindTransactionWithdrawals(
        transactionsHashes);
    List<FindTransactionRegistrations> registrations = getFindTransactionRegistrations(
        transactionsHashes);
    List<FindTransactionDeregistrations> deregistrations = getFindTransactionDeregistrations(
        transactionsHashes);
    List<FindTransactionDelegations> delegations = getFindTransactionDelegations(
        transactionsHashes);
    List<TransactionMetadataDto> votes = getTransactionMetadataDtos(
        transactionsHashes);
    List<FindTransactionPoolRegistrationsData> poolsData = getTransactionPoolRegistrationsData(
        transactionsHashes);
    List<FindTransactionPoolOwners> poolsOwners = getFindTransactionPoolOwners(
        transactionsHashes);
    List<FindTransactionPoolRelays> poolsRelays = getFindTransactionPoolRelays(
        transactionsHashes);
    List<FindPoolRetirements> poolRetirements = getFindPoolRetirements(
        transactionsHashes);
    var parseInputsRow = DataMapper.parseInputsRowFactory();
    var parseOutputsRow = DataMapper.parseOutputsRowFactory();
    var parseWithdrawalsRow = DataMapper.parseWithdrawalsRowFactory();
    var parseRegistrationsRow = DataMapper.parseRegistrationsRowFactory();
    var parseDeregistrationsRow = DataMapper.parseDeregistrationsRowFactory();
    var parseDelegationsRow = DataMapper.parseDelegationsRowFactory();
    var parsePoolRetirementRow = DataMapper.parsePoolRetirementRowFactory();
    var parseVoteRow = DataMapper.parseVoteRowFactory();
    var parsePoolRegistrationsRows = DataMapper.parsePoolRegistrationsRowsFactory();
    transactionsMap = populateTransactionField(transactionsMap, inputs, parseInputsRow);
    transactionsMap = populateTransactionField(transactionsMap, outputs, parseOutputsRow);
    transactionsMap = populateTransactionField(transactionsMap, withdrawals, parseWithdrawalsRow);
    transactionsMap = populateTransactionField(transactionsMap, registrations,
        parseRegistrationsRow);
    transactionsMap = populateTransactionField(transactionsMap, deregistrations,
        parseDeregistrationsRow);
    transactionsMap = populateTransactionField(transactionsMap, delegations, parseDelegationsRow);
    transactionsMap = populateTransactionField(transactionsMap, poolRetirements,
        parsePoolRetirementRow);
    transactionsMap = populateTransactionField(transactionsMap, votes, parseVoteRow);
    List<TransactionPoolRegistrations> mappedPoolRegistrations = mapToTransactionPoolRegistrations(
        poolsData, poolsOwners, poolsRelays);
    transactionsMap = populateTransactionField(transactionsMap, mappedPoolRegistrations,
        parsePoolRegistrationsRows);
    return new ArrayList<>(transactionsMap.values());
  }

  @Override
  public PopulatedTransaction findTransactionByHashAndBlock(String hash,
      Long blockNumber, String blockHash) {
    List<Tx> txList = blockRepository.findTransactionByHashAndBlock(hash, blockHash);
    if(txList.isEmpty()) {
      log.debug(
          "[findTransactionByHashAndBlock] No transactions found for hash {} and block {}",
          hash, blockHash);
      return null;
    }

    log.debug(
        "[findTransactionByHashAndBlock] Found {} transactions", txList.size());
      Map<String, PopulatedTransaction> transactionsMap = mapTransactionsToDict(
          parseTransactionRows(txList));
      return populateTransactions(transactionsMap).get(0);
  }

  @Override
  public List<FindTransactionsInputs> getFindTransactionsInputs(List<String> transactionsHashes) {
    log.debug("[findTransactionsInputs] with parameters {}", transactionsHashes);
    return txRepository.findTransactionsInputs(transactionsHashes);
  }

  @Override
  public List<FindPoolRetirements> getFindPoolRetirements(List<String> transactionsHashes) {
    return poolRetireRepository.findPoolRetirements(
        transactionsHashes);
  }

  @Override
  public List<FindTransactionPoolRelays> getFindTransactionPoolRelays(
      List<String> transactionsHashes) {
    return poolUpdateRepository.findTransactionPoolRelays(
        transactionsHashes);
  }

  @Override
  public List<FindTransactionPoolOwners> getFindTransactionPoolOwners(
      List<String> transactionsHashes) {
    return poolUpdateRepository.findTransactionPoolOwners(
        transactionsHashes);
  }

  @Override
  public List<FindTransactionPoolRegistrationsData> getTransactionPoolRegistrationsData(
      List<String> transactionsHashes) {
    return getFindTransactionPoolRegistrationsData(
        transactionsHashes);
  }

  @Override
  public List<FindTransactionPoolRegistrationsData> getFindTransactionPoolRegistrationsData(
      List<String> transactionsHashes) {
    return poolUpdateRepository.findTransactionPoolRegistrationsData(
        transactionsHashes);
  }

  @Override
  public List<TransactionMetadataDto> getTransactionMetadataDtos(List<String> transactionsHashes) {
    return txMetadataRepository.findTransactionMetadata(
        transactionsHashes);
  }

  @Override
  public List<FindTransactionDelegations> getFindTransactionDelegations(
      List<String> transactionsHashes) {
    return delegationRepository.findTransactionDelegations(
        transactionsHashes);
  }

  @Override
  public List<FindTransactionDeregistrations> getFindTransactionDeregistrations(
      List<String> transactionsHashes) {
    return stakeDeregistrationRepository.findTransactionDeregistrations(
        transactionsHashes);
  }

  @Override
  public List<FindTransactionRegistrations> getFindTransactionRegistrations(
      List<String> transactionsHashes) {
    return stakeDeregistrationRepository.findTransactionRegistrations(
        transactionsHashes);
  }

  @Override
  public List<FindTransactionWithdrawals> getFindTransactionWithdrawals(
      List<String> transactionsHashes) {
    return txRepository.findTransactionWithdrawals(transactionsHashes);
  }

  @Override
  public List<FindTransactionsOutputs> getFindTransactionsOutputs(
      List<String> transactionsHashes) {
    return txRepository.findTransactionsOutputs(
        transactionsHashes);
  }
}