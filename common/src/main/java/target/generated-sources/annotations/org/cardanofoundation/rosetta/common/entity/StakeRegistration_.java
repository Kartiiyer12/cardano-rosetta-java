package org.cardanofoundation.rosetta.common.entity;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import javax.annotation.Generated;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StakeRegistration.class)
public abstract class StakeRegistration_ extends org.cardanofoundation.rosetta.common.entity.BaseEntity_ {

	public static volatile SingularAttribute<StakeRegistration, Integer> certIndex;
	public static volatile SingularAttribute<StakeRegistration, Integer> epochNo;
	public static volatile SingularAttribute<StakeRegistration, Tx> tx;
	public static volatile SingularAttribute<StakeRegistration, StakeAddress> addr;

	public static final String CERT_INDEX = "certIndex";
	public static final String EPOCH_NO = "epochNo";
	public static final String TX = "tx";
	public static final String ADDR = "addr";

}

