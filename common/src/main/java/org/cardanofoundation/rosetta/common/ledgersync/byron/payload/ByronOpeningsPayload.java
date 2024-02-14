package org.cardanofoundation.rosetta.common.ledgersync.byron.payload;

import org.cardanofoundation.rosetta.common.ledgersync.byron.ByronSscCert;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class ByronOpeningsPayload implements ByronSscPayload {

  public static final String TYPE = "ByronOpeningsPayload";

  private Map<String, String> sscOpens;
  private List<ByronSscCert> sscCerts;

  @Override
  public String getType() {
    return TYPE;
  }
}