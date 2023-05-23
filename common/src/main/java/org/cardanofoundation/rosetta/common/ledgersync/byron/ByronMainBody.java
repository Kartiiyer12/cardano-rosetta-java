package org.cardanofoundation.rosetta.common.ledgersync.byron;

import org.cardanofoundation.rosetta.common.ledgersync.byron.payload.ByronDlgPayload;
import org.cardanofoundation.rosetta.common.ledgersync.byron.payload.ByronSscPayload;
import org.cardanofoundation.rosetta.common.ledgersync.byron.payload.ByronTxPayload;
import org.cardanofoundation.rosetta.common.ledgersync.byron.payload.ByronUpdatePayload;
import java.util.List;
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
public class ByronMainBody {
  private List<ByronTxPayload> txPayload;
  private ByronSscPayload sscPayload;
  private List<ByronDlgPayload> dlgPayload;
  private ByronUpdatePayload updPayload;
}
