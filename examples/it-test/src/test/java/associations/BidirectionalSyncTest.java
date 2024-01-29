/* (c) https://github.com/MontiCore/monticore */
package associations;

import associations.test_infrastructure.AssociationsMockTestSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BidirectionalSyncTest extends AssociationsMockTestSetup {
  public BidirectionalSyncTest() {
    super(1);
  }

  @Test
  public void testSettingInBuildSyncs(){
    Log.init();
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
    Log.init();
    // Build rhs1 <-> lhs1 in Client
    getClients(0).activate();
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs1 = AssociationsManager.optToManyLhsBuilder().build().get();

    // change left side
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

  @Test
  public void testSettingAfterBuild2Syncs(){
    Log.init();
    // Build rhs1 <-> lhs1 in Client
    getClients(0).activate();
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs1 = AssociationsManager.optToManyLhsBuilder().build().get();

    // change right side
    rhs1.setOptToManyLhs(lhs1);

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
  public void testDeleteOptToManySyncs(){
    Log.init();
    getClients(0).activate();
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs1 = AssociationsManager.optToManyLhsBuilder().optToManyRhs(Set.of(rhs1)).build().get();

    // ensure the server syncs correctly
    getBackend().activate();
    OptToManyRhs rhs1Server = AssociationsManager.getOptToManyRhsSure(rhs1.gemId);
    OptToManyLhs lhs1Server = AssociationsManager.getOptToManyLhsSure(lhs1.gemId);

    assertEquals(lhs1Server.gemId, rhs1Server.getOptToManyLhsGem().getGemId());
    assertTrue(lhs1Server.containsOptToManyRhs(rhs1Server));

    // delete in client
    getClients(0).activate();
    rhs1.setOptToManyLhsAbsent();

    // ensure the server syncs correctly
    getBackend().activate();

    assertFalse(rhs1Server.getOptToManyLhsGem().isPresent());
    assertEquals(0, lhs1Server.sizeOptToManyRhss());

  }

  @Test
  public void testDeleteOptToOptSyncs(){
    Log.init();
    getClients(0).activate();
    OptToOptRhs rhs1 = AssociationsManager.optToOptRhsBuilder().build().get();
    OptToOptLhs lhs1 = AssociationsManager.optToOptLhsBuilder().optToOptRhs(rhs1).build().get();

    // ensure the server syncs correctly
    getBackend().activate();
    OptToOptRhs rhs1Server = AssociationsManager.getOptToOptRhsSure(rhs1.gemId);
    OptToOptLhs lhs1Server = AssociationsManager.getOptToOptLhsSure(lhs1.gemId);

    assertEquals(lhs1Server.gemId, rhs1Server.getOptToOptLhsGem().getGemId());
    assertEquals(rhs1Server.gemId, lhs1Server.getOptToOptRhsGem().getGemId());

    // delete in client
    getClients(0).activate();
    lhs1.setOptToOptRhsAbsent();

    // ensure the server syncs correctly
    getBackend().activate();

    assertFalse(rhs1Server.getOptToOptLhsGem().isPresent());
    assertFalse(lhs1Server.getOptToOptRhsGem().isPresent());

  }

  @Test
  public void testDeleteOptToOneSyncs(){
    Log.init();
    getClients(0).activate();
    OptToOneRhs rhs1 = AssociationsManager.optToOneRhsBuilder().build().get();
    OptToOneLhs lhs1 = AssociationsManager.optToOneLhsBuilder().optToOneRhs(rhs1).build().get();

    // ensure the server syncs correctly
    getBackend().activate();
    OptToOneRhs rhs1Server = AssociationsManager.getOptToOneRhsSure(rhs1.gemId);
    OptToOneLhs lhs1Server = AssociationsManager.getOptToOneLhsSure(lhs1.gemId);

    assertEquals(lhs1Server.gemId, rhs1Server.getOptToOneLhsGem().getGemId());
    assertEquals(rhs1Server.gemId, lhs1Server.getOptToOneRhsGem().getGemId());

    // change in client
    getClients(0).activate();
    OptToOneRhs rhs2 = AssociationsManager.optToOneRhsBuilder().build().get();
    lhs1.setOptToOneRhs(rhs2);

    // ensure the server syncs correctly
    getBackend().activate();
    OptToOneRhs rhs2Server = AssociationsManager.getOptToOneRhsSure(rhs2.gemId);

    assertEquals(lhs1Server.gemId, rhs2Server.getOptToOneLhsGem().getGemId());
    assertEquals(rhs2Server.gemId, lhs1Server.getOptToOneRhsGem().getGemId());
    assertFalse(rhs1Server.getOptToOneLhsGem().isPresent());

  }

  @Test
  public void testDeleteManyToManySyncs(){
    Log.init();
    getClients(0).activate();
    ManyToManyRhs rhs1 = AssociationsManager.manyToManyRhsBuilder().build().get();
    ManyToManyLhs lhs1 = AssociationsManager.manyToManyLhsBuilder().manyToManyRhs(Set.of(rhs1)).build().get();

    // ensure the server syncs correctly
    getBackend().activate();
    ManyToManyRhs rhs1Server = AssociationsManager.getManyToManyRhsSure(rhs1.gemId);
    ManyToManyLhs lhs1Server = AssociationsManager.getManyToManyLhsSure(lhs1.gemId);

    assertTrue(rhs1Server.containsManyToManyLhs(lhs1Server));
    assertTrue(lhs1Server.containsManyToManyRhs(rhs1Server));

    // delete in client
    getClients(0).activate();
    lhs1.removeManyToManyRhs(rhs1Server);

    // ensure the server syncs correctly
    getBackend().activate();

    assertEquals(0, rhs1Server.sizeManyToManyLhss());
    assertEquals(0, lhs1Server.sizeManyToManyRhss());

  }

}
