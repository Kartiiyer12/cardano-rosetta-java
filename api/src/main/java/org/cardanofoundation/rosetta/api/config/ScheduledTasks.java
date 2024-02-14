package org.cardanofoundation.rosetta.api.config;

import static org.cardanofoundation.rosetta.api.common.constants.Constants.REDIS_TTL_MEMPOOL;

import com.bloxbean.cardano.client.transaction.util.TransactionUtil;
import com.bloxbean.cardano.yaci.helper.LocalTxMonitorClient;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.rosetta.api.common.constants.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
@Slf4j
@Profile("!test-integration")
public class ScheduledTasks {

//  private final LocalTxMonitorClient localTxMonitorClient;

//  public ScheduledTasks(LocalTxMonitorClient localTxMonitorClient) {
//    this.localTxMonitorClient = localTxMonitorClient;
//  }

  @Scheduled(fixedDelayString = "${scheduler.time:1000}")
  public void scheduleTaskWithFixedRate() {
//    List<String> transactionHashes = getAllTransactionsInMempool();
//    log.info("There are {} transactions in mempool", transactionHashes.size());
//    Map<String, String> txHashWithData = new HashMap<>();
//    for (String transactionHash : transactionHashes) {
//      String pendingData = redisTemplate.opsForValue()
//          .get(Constants.REDIS_PREFIX_PENDING + transactionHash);
//      String txData = Objects.isNull(pendingData) ? redisTemplate.opsForValue()
//          .get(Constants.REDIS_PREFIX_MEMPOOL + transactionHash) : pendingData;
//      txHashWithData.put(Constants.REDIS_PREFIX_MEMPOOL + transactionHash, txData);
//    }
//    redisTemplate.delete(redisTemplate.keys(Constants.REDIS_PREFIX_MEMPOOL + "*"));
//    txHashWithData.entrySet().stream().forEach((entry) -> {
//      redisTemplate.opsForValue().set(entry.getKey(), entry.getValue(), REDIS_TTL_MEMPOOL);
//    });
  }
//
//  public List<String> getAllTransactionsInMempool() {
//    return localTxMonitorClient.acquireAndGetMempoolTransactionsAsMono()
//        .blockOptional()
//        .orElse(Collections.emptyList()).stream().map(TransactionUtil::getTxHash).toList();
//  }
}