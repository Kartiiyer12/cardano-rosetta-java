package org.cardanofoundation.rosetta.common.entity;

import jakarta.persistence.ConstraintMode;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.cardanofoundation.rosetta.common.validation.Lovelace;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigInteger;
import java.util.Objects;

@Entity
//@Table(name = "withdrawal", uniqueConstraints = {
//    @UniqueConstraint(name = "unique_slot_leader",
//        columnNames = {"addr_id", "tx_id"}
//    )
//})
@Table(name = "withdrawal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Withdrawal extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "addr_id", nullable = false,
      foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
  @EqualsAndHashCode.Exclude
  private StakeAddress addr;

  @Column(name = "addr_id", updatable = false, insertable = false)
  private Long stakeAddressId;

  @Column(name = "amount", nullable = false, precision = 20)
  @Lovelace
  @Digits(integer = 20, fraction = 0)
  private BigInteger amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "redeemer_id",
      foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
  @EqualsAndHashCode.Exclude
  private Redeemer redeemer;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tx_id", nullable = false,
      foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT, name = "none"))
  @EqualsAndHashCode.Exclude
  private Tx tx;
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    Withdrawal that = (Withdrawal) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
