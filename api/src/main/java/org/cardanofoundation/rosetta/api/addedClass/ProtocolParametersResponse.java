package org.cardanofoundation.rosetta.api.addedClass;

public interface ProtocolParametersResponse {
    Long getCoinsPerUtxoSize();
    Integer getMaxTxSize();
    Long getMaxValSize();
    Long getKeyDeposit();
    Integer getMaxCollateralInputs();
    Integer getMinFeeCoefficient();
    Integer getMinFeeConstant();
    Long getMinPoolCost();
    Long getPoolDeposit();
    Integer getProtocolMajor();
}
