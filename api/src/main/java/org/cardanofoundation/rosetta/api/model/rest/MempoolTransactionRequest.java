package org.cardanofoundation.rosetta.api.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cardanofoundation.rosetta.api.model.TransactionIdentifier;

/**
 * @author Sotatek-HoangNguyen9
 * @since 12/04/2023 17:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MempoolTransactionRequest {

    @JsonProperty("network_identifier")
    @NotNull(message = "NetworkIdentifier must not be null")
    private NetworkIdentifier networkIdentifier;

    @JsonProperty("transaction_identifier")
    @NotNull(message = "TransactionIdentifier must not be null")
    @Valid
    private TransactionIdentifier transactionIdentifier;

}