package org.cardanofoundation.rosetta.common.entity;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import javax.annotation.Generated;
import org.cardanofoundation.rosetta.common.enumeration.ScriptType;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Script.class)
public abstract class Script_ extends org.cardanofoundation.rosetta.common.entity.BaseEntity_ {

	public static volatile SingularAttribute<Script, Tx> tx;
	public static volatile SingularAttribute<Script, byte[]> bytes;
	public static volatile SingularAttribute<Script, Integer> serialisedSize;
	public static volatile SingularAttribute<Script, String> json;
	public static volatile SingularAttribute<Script, ScriptType> type;
	public static volatile SingularAttribute<Script, String> hash;

	public static final String TX = "tx";
	public static final String BYTES = "bytes";
	public static final String SERIALISED_SIZE = "serialisedSize";
	public static final String JSON = "json";
	public static final String TYPE = "type";
	public static final String HASH = "hash";

}

