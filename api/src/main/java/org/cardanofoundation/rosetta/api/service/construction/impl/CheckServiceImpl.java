package org.cardanofoundation.rosetta.api.service.construction.impl;

import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.rosetta.api.construction.data.NetWork;
import org.cardanofoundation.rosetta.api.construction.data.Const;
import org.cardanofoundation.rosetta.api.exception.ExceptionFactory;
import org.cardanofoundation.rosetta.api.service.construction.CardanoService;
import org.cardanofoundation.rosetta.api.service.construction.CheckService;
import org.cardanofoundation.rosetta.api.model.rest.NetworkIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class CheckServiceImpl implements CheckService {
    @Autowired
    CardanoService cardanoService;

    @Override
    public void withNetworkValidation(NetworkIdentifier networkIdentifier) {
        log.debug("[withNetworkValidation] About to validate requests network identifier parameter', networkIdentifier");
        String blockchain = networkIdentifier.getBlockchain();
        String network = networkIdentifier.getNetwork();

        if (!blockchain.equals(Const.CARDANO)) {
            log.error("[withNetworkValidation] Blockchain parameter {} is not cardano: ", blockchain);
            throw ExceptionFactory.invalidBlockchainError();
        }

        boolean networkExists = getSupportedNetwork("mainnet", 764824073).getNetworkId().equals(network);
        if (!networkExists) {
            log.error("[withNetworkValidation] Network parameter {} is not supported: ", network);
            throw new IllegalArgumentException("networkNotFoundError");
        }
        log.debug("[withNetworkValidation] Network parameters are within expected");
    }

    @Override
    public NetWork getSupportedNetwork(String networkId, Integer networkMagic) {
        if (networkId.equals("mainnet")) return new NetWork(networkId);
        if (networkMagic == Const.PREPROD_NETWORK_MAGIC) return new NetWork(Const.PREPROD);
        if (networkMagic == Const.PREVIEW_NETWORK_MAGIC) return new NetWork(Const.PREVIEW);
        return new NetWork(networkId, networkMagic);
    }
}
