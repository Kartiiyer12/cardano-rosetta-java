package org.cardanofoundation.rosetta.api.constructionApiService;

import org.cardanofoundation.rosetta.api.addedClass.AddedNetWork;
import org.cardanofoundation.rosetta.api.model.rest.NetworkIdentifier;
import org.springframework.stereotype.Repository;


public interface CheckService {
    void withNetworkValidation(NetworkIdentifier networkIdentifier);

    AddedNetWork getSupportedNetwork(String networkId, Integer networkMagic);
}
