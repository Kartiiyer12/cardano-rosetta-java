package org.cardanofoundation.rosetta.consumer.repository;

import org.cardanofoundation.rosetta.common.entity.PoolHash;
import org.cardanofoundation.rosetta.common.entity.PoolMetadataRef;
import org.cardanofoundation.rosetta.common.entity.PoolMetadataRef_;
import org.cardanofoundation.rosetta.common.entity.Tx;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface PoolMetadataRefRepository extends JpaRepository<PoolMetadataRef, Long> {

  @EntityGraph(attributePaths = PoolMetadataRef_.POOL_HASH)
  @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
  Optional<PoolMetadataRef> findPoolMetadataRefByPoolHashAndUrlAndHash(
      PoolHash poolHash, String url, String hash);

  @Modifying
  void deleteAllByRegisteredTxIn(Collection<Tx> registeredTxs);
}
