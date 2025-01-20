package builder;

import builder.test_infrastructure.BuilderSQLDBTestSetup;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.Gem;
import umlp.backendrte.common.LoadingState;
import umlp.test_rte.ClientSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// We want to test the following situation:
/// * Server creates O1
/// * Client creates O2 and links that to O1 via GemID
/// * assert that O1 got updated with link to O2
public class SyncLinkCreationTest extends BuilderSQLDBTestSetup {

  public SyncLinkCreationTest() {
    super(ClientSetup.of(1));
  }

  @Test
  void testSyncLinking() {
    //server creates O1
    getBackend().activate();

    var o1 = BuilderManager.linkObjectBuilder().build().orElseThrow();
    var o1GemId = o1.getGemId();

    //client creates O2 and links to O1
    getClients(0).activate();

    var o1Gem = new Gem<LinkObject>(o1GemId, LoadingState.EMPTY);
    var o2 = BuilderManager.linkObjectBuilder().linkGem(o1Gem).build().orElseThrow();
    var o2GemId = o2.getGemId();

    //O1 should contain link to O2
    var linkGem = o1.getLinkObjectGem();
    assertEquals(o2GemId, linkGem.getGemId());
  }
}
