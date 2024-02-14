package org.cardanofoundation.rosetta.api.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cardanofoundation.rosetta.api.model.Operation;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sotatek-HoangNguyen9
 * @since 12/04/2023 17:14
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConstructionParseResponse {
    @JsonProperty("network_identifier")
    private NetworkIdentifier networkIdentifier;

    public ConstructionParseResponse(NetworkIdentifier networkIdentifier, List<Operation> operations, List<AccountIdentifier> accountIdentifierSigners) {
        this.networkIdentifier = networkIdentifier;
        this.operations = operations;
        this.signers = signers;
        this.accountIdentifierSigners = accountIdentifierSigners;
    }

    @JsonProperty("operations")
    @Valid
    private List<Operation> operations = new ArrayList<>();

    @JsonProperty("signers")
    @Valid
    private List<String> signers = null;

    @JsonProperty("account_identifier_signers")
    @Valid
    private List<AccountIdentifier> accountIdentifierSigners = new ArrayList<>();

    @JsonProperty("metadata")
    private Object metadata;

}