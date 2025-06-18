package associations;

import associations.commands.associations.OptToMany_set;
import associations.test_infrastructure.AssociationsMockTestSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.CommandManager;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BidirectionalCommandSyncTest extends AssociationsMockTestSetup {
  public BidirectionalCommandSyncTest() {
    super(1);
  }

  @Test
  public void testSettingInBuildSyncs(){
    Log.init();
    // Build rhs1 <-> lhs1 in Client
    getClients(0).activate();
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs1 = AssociationsManager.optToManyLhsBuilder().build().get();

    // Ensure both are empty
    assertFalse(rhs1.isPresentOptToManyLhs());
    assertTrue(lhs1.getOptToManyRhsList().isEmpty());

    // Simulating rhs.setLhs(rhs) method call from the client
    CommandManager.executeCommand(new OptToMany_set(-1, rhs1.gemId, lhs1.gemId, rhs1.gemId,0,0));

    // ensure the server syncs correctly
    getBackend().activate();
    OptToManyRhs rhs1Server = AssociationsManager.getOptToManyRhsSure(rhs1.gemId);
    OptToManyLhs lhs1Server = AssociationsManager.getOptToManyLhsSure(lhs1.gemId);

    assertEquals(lhs1Server.gemId, rhs1Server.getOptToManyLhsGem().getGemId());
    assertTrue(lhs1Server.containsOptToManyRhs(rhs1Server));
    assertEquals(1, lhs1Server.getOptToManyRhsList().size());
  }
}
