package org.cardanofoundation.rosetta.common.entity;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigInteger;
import javax.annotation.Generated;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Withdrawal.class)
public abstract class Withdrawal_ extends org.cardanofoundation.rosetta.common.entity.BaseEntity_ {

	public static volatile SingularAttribute<Withdrawal, BigInteger> amount;
	public static volatile SingularAttribute<Withdrawal, Tx> tx;
	public static volatile SingularAttribute<Withdrawal, Long> stakeAddressId;
	public static volatile SingularAttribute<Withdrawal, Redeemer> redeemer;
	public static volatile SingularAttribute<Withdrawal, StakeAddress> addr;

	public static final String AMOUNT = "amount";
	public static final String TX = "tx";
	public static final String STAKE_ADDRESS_ID = "stakeAddressId";
	public static final String REDEEMER = "redeemer";
	public static final String ADDR = "addr";

}

