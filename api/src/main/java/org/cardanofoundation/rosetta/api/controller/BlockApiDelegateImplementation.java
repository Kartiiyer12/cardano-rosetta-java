package org.cardanofoundation.rosetta.api.controller;

import lombok.RequiredArgsConstructor;
import org.cardanofoundation.rosetta.api.model.rest.BlockRequest;
import org.cardanofoundation.rosetta.api.model.rest.BlockResponse;
import org.cardanofoundation.rosetta.api.model.rest.BlockTransactionRequest;
import org.cardanofoundation.rosetta.api.model.rest.BlockTransactionResponse;
import org.cardanofoundation.rosetta.api.service.BlockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BlockApiDelegateImplementation implements BlockApiDelegate {

  private final BlockService blockService;

  @Override
  public ResponseEntity<BlockResponse> block(
      @RequestBody BlockRequest blockRequest) {
    return ResponseEntity.ok(blockService.getBlockByBlockRequest(blockRequest));
  }

  @Override
  public ResponseEntity<BlockTransactionResponse> blockTransaction(
      @RequestBody BlockTransactionRequest blockTransactionRequest) {
    return ResponseEntity.ok(blockService.getBlockTransaction(blockTransactionRequest));
  }
}
