package org.cardanofoundation.rosetta.api.block.model.entity;

import com.bloxbean.cardano.yaci.core.model.certs.StakeCredType;
import com.bloxbean.cardano.yaci.core.model.governance.DrepType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delegation_vote")
@IdClass(DrepVoteDelegationId.class)
public class DrepVoteDelegationEntity {

  @jakarta.persistence.Id
  @Column(name = "tx_hash")
  private String txHash;

  @jakarta.persistence.Id
  @Column(name = "cert_index")
  private long certIndex;

  @Column(name = "slot")
  private Long slot;

  @Column(name = "block")
  private Long blockNumber;

  @Column(name = "block_time")
  private Long blockTime;

  @Column(name = "update_datetime")
  private LocalDateTime updateDateTime;

  @Column(name = "address")
  private String address;

  @Column(name = "drep_hash") // actual drep id as hex hash
  private String drepHash;

  @Column(name = "drep_id") // bech 32
  private String drepId;

  @Column(name = "drep_type")
  @Enumerated(EnumType.STRING)
  private DrepType drepType;

  @Column(name = "credential")
  private String credential;

  @Column(name = "cred_type")
  @Enumerated(EnumType.STRING)
  private StakeCredType credType;

  @Column(name = "epoch")
  private Integer epoch;

}
