package org.cardanofoundation.rosetta.common.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.cardanofoundation.rosetta.common.validation.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "param_proposal", uniqueConstraints = {
    @UniqueConstraint(name = "unique_param_proposal",
        columnNames = {"key", "registered_tx_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ParamProposal extends BaseEntity {

  @Column(name = "epoch_no", nullable = false)
  @Word31Type
  private Integer epochNo;

  @Column(name = "key", nullable = false, length = 56)
  @Hash28Type
  private String key;

  @Column(name = "min_fee_a", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger minFeeA;

  @Column(name = "min_fee_b", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger minFeeB;

  @Column(name = "max_block_size", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger maxBlockSize;

  @Column(name = "max_tx_size", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger maxTxSize;

  @Column(name = "max_bh_size", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger maxBhSize;

  @Column(name = "key_deposit")
  @Lovelace
  @Digits(integer = 20, fraction = 0)
  private BigInteger keyDeposit;

  @Column(name = "pool_deposit", precision = 20)
  @Lovelace
  @Digits(integer = 20, fraction = 0)
  private BigInteger poolDeposit;

  @Column(name = "max_epoch", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger maxEpoch;

  @Column(name = "optimal_pool_count", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger optimalPoolCount;

  @Column(name = "influence")
  private Double influence;

  @Column(name = "monetary_expand_rate")
  private Double monetaryExpandRate;

  @Column(name = "treasury_growth_rate")
  private Double treasuryGrowthRate;

  @Column(name = "decentralisation")
  private Double decentralisation;

  @Column(name = "entropy", length = 64)
  @Hash32Type
  private String entropy;

  @Column(name = "protocol_major")
  @Word31Type
  private Integer protocolMajor;

  @Column(name = "protocol_minor")
  @Word31Type
  private Integer protocolMinor;

  @Column(name = "min_utxo_value", precision = 20)
  @Lovelace
  @Digits(integer = 20, fraction = 0)
  private BigInteger minUtxoValue;

  @Column(name = "min_pool_cost", precision = 20)
  @Lovelace
  @Digits(integer = 20, fraction = 0)
  private BigInteger minPoolCost;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "cost_model_id",
      foreignKey = @ForeignKey(name = "param_proposal_cost_model_id_fkey"))
  @EqualsAndHashCode.Exclude
  private CostModel costModel;

  @Column(name = "price_mem")
  private Double priceMem;

  @Column(name = "price_step")
  private Double priceStep;

  @Column(name = "max_tx_ex_mem", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger maxTxExMem;

  @Column(name = "max_tx_ex_steps", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger maxTxExSteps;

  @Column(name = "max_block_ex_mem", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger maxBlockExMem;

  @Column(name = "max_block_ex_steps", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger maxBlockExSteps;

  @Column(name = "max_val_size", precision = 20)
  @Word64Type
  @Digits(integer = 20, fraction = 0)
  private BigInteger maxValSize;

  @Column(name = "collateral_percent")
  @Word31Type
  private Integer collateralPercent;

  @Column(name = "max_collateral_inputs")
  @Word31Type
  private Integer maxCollateralInputs;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "registered_tx_id", nullable = false,
      foreignKey = @ForeignKey(name = "param_proposal_registered_tx_id_fkey"))
  @EqualsAndHashCode.Exclude
  private Tx registeredTx;

  @Column(name = "coins_per_utxo_size")
  @Lovelace
  @Digits(integer = 20, fraction = 0)
  private BigInteger coinsPerUtxoSize;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    ParamProposal that = (ParamProposal) o;

    if (this.hashCode() == that.hashCode()) {
      return true;
    }

    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getHashCode(minFeeA) + getHashCode(minFeeB) + getHashCode(maxBlockSize) +
        getHashCode(maxTxSize) + getHashCode(maxBhSize) + getHashCode(keyDeposit) +
        getHashCode(poolDeposit) + getHashCode(maxEpoch) + getHashCode(optimalPoolCount) +
        getHashCode(influence) + getHashCode(monetaryExpandRate) + getHashCode(treasuryGrowthRate) +
        getHashCode(decentralisation) + getHashCode(entropy) + getHashCode(protocolMajor) +
        getHashCode(protocolMinor) + getHashCode(minUtxoValue) + getHashCode(minPoolCost) +
        getHashCode(costModel) + getHashCode(priceMem) + getHashCode(priceStep) +
        getHashCode(maxTxExMem) + getHashCode(maxTxExSteps) + getHashCode(maxBlockExMem) +
        getHashCode(maxBlockExSteps) + getHashCode(maxValSize) + getHashCode(collateralPercent) +
        getHashCode(maxCollateralInputs) + getHashCode(registeredTx) + getHashCode(coinsPerUtxoSize);
  }

  private int getHashCode(Object o) {
    if (Objects.isNull(o)) {
      return BigInteger.ZERO.intValue();
    }

    return o.hashCode();
  }

}
