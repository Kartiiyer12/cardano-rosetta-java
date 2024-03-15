package org.cardanofoundation.rosetta.api.block.model.entity;

import com.bloxbean.cardano.yaci.core.model.certs.CertificateType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stake_registration")
@IdClass(StakeRegistrationId.class)
public class StakeRegistrationEntity extends BlockAwareEntity {
    @Id
    @Column(name = "tx_hash")
    private String txHash;

    @Id
    @Column(name = "cert_index")
    private long certIndex;

    @Column(name = "credential")
    private String credential;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private CertificateType type;

    @Column(name = "address")
    private String address;

    @Column(name = "epoch")
    private Integer epoch;

    @Column(name = "slot")
    private Long slot;

    @Column(name = "block_hash")
    private String blockHash;
}