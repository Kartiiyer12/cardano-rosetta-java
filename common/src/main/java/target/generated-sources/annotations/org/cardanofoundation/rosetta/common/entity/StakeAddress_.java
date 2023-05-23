package org.cardanofoundation.rosetta.common.entity;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigInteger;
import javax.annotation.Generated;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StakeAddress.class)
public abstract class StakeAddress_ extends org.cardanofoundation.rosetta.common.entity.BaseEntity_ {

	public static volatile SingularAttribute<StakeAddress, String> view;
	public static volatile SingularAttribute<StakeAddress, BigInteger> availableReward;
	public static volatile SingularAttribute<StakeAddress, BigInteger> balance;
	public static volatile SingularAttribute<StakeAddress, String> scriptHash;
	public static volatile SingularAttribute<StakeAddress, String> hashRaw;

	public static final String VIEW = "view";
	public static final String AVAILABLE_REWARD = "availableReward";
	public static final String BALANCE = "balance";
	public static final String SCRIPT_HASH = "scriptHash";
	public static final String HASH_RAW = "hashRaw";

}

