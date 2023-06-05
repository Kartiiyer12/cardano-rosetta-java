package org.cardanofoundation.rosetta.api.service.impl;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.rosetta.api.exception.ExceptionFactory;
import org.cardanofoundation.rosetta.api.mapper.DataMapper;
import org.cardanofoundation.rosetta.api.model.rest.AccountBalanceRequest;
import org.cardanofoundation.rosetta.api.model.rest.AccountBalanceResponse;
import org.cardanofoundation.rosetta.api.model.rest.AccountCoinsRequest;
import org.cardanofoundation.rosetta.api.model.rest.AccountCoinsResponse;
import org.cardanofoundation.rosetta.api.model.rest.Currency;
import org.cardanofoundation.rosetta.api.projection.dto.BlockUtxos;
import org.cardanofoundation.rosetta.api.service.AccountService;
import org.cardanofoundation.rosetta.api.service.BlockService;
import org.cardanofoundation.rosetta.api.service.CardanoService;
import org.cardanofoundation.rosetta.api.util.Validations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final CardanoService cardanoService;
  private final BlockService blockService;

  @Override
  public AccountBalanceResponse getAccountBalance(AccountBalanceRequest accountBalanceRequest) {
    Long index = null;
    String hash = null;
    String accountAddress = accountBalanceRequest.getAccountIdentifier().getAddress();
    log.debug("[accountBalance] Request received: " + accountBalanceRequest);
    if (Objects.isNull(cardanoService.getEraAddressType(accountAddress))) {
      throw ExceptionFactory.invalidAddressError(accountAddress);
    }
    log.info("[accountBalance] Looking for block: "
        + accountBalanceRequest.getBlockIdentifier()
        + "|| 'latest'}");
    if (Objects.nonNull(accountBalanceRequest.getBlockIdentifier())){
      index = accountBalanceRequest.getBlockIdentifier().getIndex();
      hash = accountBalanceRequest.getBlockIdentifier().getHash();
    }
    
    AccountBalanceResponse accountBalanceResponse = blockService.findBalanceDataByAddressAndBlock(
        accountAddress,
        index,
        hash);
    log.debug("[accountBalance] About to return " + accountBalanceResponse);
    return accountBalanceResponse;

  }

  @Override
  public AccountCoinsResponse getAccountCoins(AccountCoinsRequest accountCoinsRequest) {
    String accountAddress = accountCoinsRequest.getAccountIdentifier().getAddress();
    List<Currency> currencies = accountCoinsRequest.getCurrencies();

    log.debug("[accountCoins] Request received " + accountCoinsRequest);
    if (Objects.isNull(cardanoService.getEraAddressType(accountAddress))) {
      log.debug("[accountCoins] Address isn't Era");
      throw ExceptionFactory.invalidAddressError(accountAddress);
    }
    log.debug("[accountCoins] Address is Era");
    if (Objects.nonNull(currencies)) {
      Validations.validateCurrencies(currencies);
    }
    List<Currency> currenciesRequested = Validations.filterRequestedCurrencies(currencies);
    log.debug("[accountCoins] Filter currency is " + currenciesRequested);
    BlockUtxos blockUtxos = blockService.findCoinsDataByAddress(accountAddress,
        currenciesRequested);
    log.debug("[accountCoins] blockUtxos is " + blockUtxos);
    AccountCoinsResponse response = DataMapper.mapToAccountCoinsResponse(blockUtxos);
    log.debug("[accountCoins] About to return " + response);

    return response;
  }
}
