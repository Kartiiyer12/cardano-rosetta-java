package org.cardanofoundation.rosetta.common.entity;

import java.sql.Timestamp;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.cardanofoundation.rosetta.common.validation.Hash32Type;
import org.hibernate.Hibernate;

@Entity
@Table(name = "cost_model", uniqueConstraints = {
    @UniqueConstraint(name = "unique_cost_model",
        columnNames = {"hash"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class CostModel extends BaseEntity {

  @Column(name = "costs", nullable = false, length = 65535)
  private String costs;


  @Column(name = "hash", nullable = false, length = 64)
  @Hash32Type
  private String hash;
  @Column(name = "created_at")
  private Timestamp createdAt;
  @Column(name = "is_deleted")
  private Boolean isDeleted;

  @Column(name = "updated_at")
  private Timestamp updatedAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    CostModel costModel = (CostModel) o;

    if (Objects.isNull(id) && Objects.nonNull(hash)) {
      return hash.equals(costModel.getHash());
    }

    return id != null && Objects.equals(id, costModel.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
