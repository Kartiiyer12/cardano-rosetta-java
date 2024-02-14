package org.cardanofoundation.rosetta.api.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sotatek-HoangNguyen9
 * @since 12/04/2023 17:15
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ConstructionSubmitRequest {
    @JsonProperty("network_identifier")
    private NetworkIdentifier networkIdentifier;

    @JsonProperty("signed_transaction")
    private String signedTransaction;
}