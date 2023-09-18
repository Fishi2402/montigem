package associations;

import associations.test_infrastructure.AssociationsMockTestSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BidirectionalSyncTest extends AssociationsMockTestSetup {
  public BidirectionalSyncTest() {
    super(1);
  }

  @Disabled("https://git.rwth-aachen.de/monticore/monticore/-/issues/3534")
  @Test
  public void testSettingInBuildSyncs(){
    Log.initDEBUG();
    // Build rhs1 <-> lhs1 in Client
    getClients(0).activate();
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs1 = AssociationsManager.optToManyLhsBuilder().optToManyRhs(Set.of(rhs1)).build().get();

    assertEquals(lhs1.gemId, rhs1.getOptToManyLhsGem().getGemId());
    assertTrue(lhs1.containsOptToManyRhs(rhs1));

    // ensure the server syncs correctly
    getBackend().activate();
    OptToManyRhs rhs1Server = AssociationsManager.getOptToManyRhsSure(rhs1.gemId);
    OptToManyLhs lhs1Server = AssociationsManager.getOptToManyLhsSure(lhs1.gemId);

    assertEquals(lhs1Server.gemId, rhs1Server.getOptToManyLhsGem().getGemId());
    assertTrue(lhs1Server.containsOptToManyRhs(rhs1Server));
  }

  @Test
  public void testSettingAfterBuildSyncs(){
    Log.initDEBUG();
    // Build rhs1 <-> lhs1 in Client
    getClients(0).activate();
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs1 = AssociationsManager.optToManyLhsBuilder().build().get();

    lhs1.addOptToManyRhs(rhs1);

    assertEquals(lhs1.gemId, rhs1.getOptToManyLhsGem().getGemId());
    assertTrue(lhs1.containsOptToManyRhs(rhs1));

    // ensure the server syncs correctly
    getBackend().activate();
    OptToManyRhs rhs1Server = AssociationsManager.getOptToManyRhsSure(rhs1.gemId);
    OptToManyLhs lhs1Server = AssociationsManager.getOptToManyLhsSure(lhs1.gemId);

    assertEquals(lhs1Server.gemId, rhs1Server.getOptToManyLhsGem().getGemId());
    assertTrue(lhs1Server.containsOptToManyRhs(rhs1Server));
  }

}
