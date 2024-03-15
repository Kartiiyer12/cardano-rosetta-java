package org.cardanofoundation.rosetta.common.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionMetadata {
    @JsonProperty("size")
    Long size;
    @JsonProperty("scriptSize")
    Long scriptSize;

}