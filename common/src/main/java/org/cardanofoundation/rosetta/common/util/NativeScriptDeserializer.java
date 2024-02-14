package org.cardanofoundation.rosetta.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import lombok.SneakyThrows;
import org.cardanofoundation.rosetta.common.ledgersync.nativescript.NativeScript;

public class NativeScriptDeserializer extends JsonDeserializer<NativeScript> {

    @SneakyThrows
    @Override
    public NativeScript deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return NativeScript.deserializeJson(node.toString());
    }
}