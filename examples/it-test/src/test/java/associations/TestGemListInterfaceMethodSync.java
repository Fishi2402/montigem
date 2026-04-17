package associations;

import associations.test_infrastructure.AssociationsMockTestSetup;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGemListInterfaceMethodSync extends AssociationsMockTestSetup {

  public TestGemListInterfaceMethodSync() {
    super(1);
  }

  @Test
  public void testListInterfaceAdd() {
    // Create disconnected Objects in Backend
    getClients(0).activate();
    var manyToManyLhs = AssociationsManager.manyToManyLhsBuilder().build().get();
    var manyToManyRhs = AssociationsManager.manyToManyRhsBuilder().build().get();

    // Load disconnected Objects from Backend
    getBackend().activate();
    var client1ManyToManyLhs = AssociationsManager.getManyToManyLhsList().get(0);
    var client1ManyToManyRhs = AssociationsManager.getManyToManyRhsList().get(0);

    // Connect objects in backend
    getClients(0).activate();
    manyToManyLhs.getManyToManyRhsList().add(manyToManyRhs);
    assertEquals(1, manyToManyLhs.sizeManyToManyRhss());

    // Check if objects are connected in client i.e. client received update
    getBackend().activate();
    assertEquals(1, client1ManyToManyLhs.sizeManyToManyRhss());
  }

  @Test
  public void testListInterfaceRemove() {
    // Create disconnected Objects in Client and connect them
    getClients(0).activate();
    var manyToManyLhs = AssociationsManager.manyToManyLhsBuilder().build().get();
    var manyToManyRhs = AssociationsManager.manyToManyRhsBuilder().build().get();
    manyToManyLhs.addManyToManyRhs(manyToManyRhs);

    // Load disconnected Objects
    getBackend().activate();
    var backend1ManyToManyLhs = AssociationsManager.getManyToManyLhsList().get(0);
    var client1ManyToManyRhs = AssociationsManager.getManyToManyRhsList().get(0);

    // Disconnect objects in client
    getClients(0).activate();
    manyToManyLhs.getManyToManyRhsList().remove(0);
    assertEquals(0, manyToManyLhs.sizeManyToManyRhss());

    // Check if objects are disconnected in backend i.e. backend received update
    getBackend().activate();
    assertEquals(0, backend1ManyToManyLhs.sizeManyToManyRhss());
  }

  @Disabled("monticore/monticore#5012")
  @Test
  public void testListInterfaceSet() {
    // Create connected Objects in Client
    getClients(0).activate();
    var manyToManyLhs = AssociationsManager.manyToManyLhsBuilder().build().get();
    var manyToManyRhs = AssociationsManager.manyToManyRhsBuilder().build().get();
    var manyToManyRhs2 = AssociationsManager.manyToManyRhsBuilder().build().get();
    manyToManyLhs.addManyToManyRhs(manyToManyRhs);

    // Load connected Objects
    getBackend().activate();
    var backend1ManyToManyLhs = AssociationsManager.getManyToManyLhsList().get(0);
    assertEquals(1, backend1ManyToManyLhs.sizeManyToManyRhss());
    assertEquals(manyToManyRhs.getGemId(), backend1ManyToManyLhs.getManyToManyRhs(0).getGemId());

    // Disconnect objects in client
    getClients(0).activate();
    assertEquals(1, manyToManyLhs.sizeManyToManyRhss());
    manyToManyLhs.getManyToManyRhsList().set(0, manyToManyRhs2);
    assertEquals(1, manyToManyLhs.sizeManyToManyRhss());
    assertEquals(manyToManyRhs2.getGemId(), manyToManyLhs.getManyToManyRhs(0).getGemId());

    // Check if objects were correctly replace in backend i.e. backend received update
    getBackend().activate();
    assertEquals(1, backend1ManyToManyLhs.sizeManyToManyRhss());
    assertEquals(manyToManyRhs2.getGemId(), backend1ManyToManyLhs.getManyToManyRhs(0).getGemId());
  }

  @Disabled("monticore#5011")
  @Test
  public void testListInterfaceAddWithBackendGoingFirst() {
    // Create disconnected Objects in Backend
    getBackend().activate();
    var manyToManyLhs = AssociationsManager.manyToManyLhsBuilder().build().get();
    var manyToManyRhs = AssociationsManager.manyToManyRhsBuilder().build().get();

    // Load disconnected Objects from Backend
    getClients(0).activate();
    var client1ManyToManyLhs = AssociationsManager.getManyToManyLhsList().get(0);
    var client1ManyToManyRhs = AssociationsManager.getManyToManyRhsList().get(0);

    // Connect objects in backend
    getBackend().activate();
    manyToManyLhs.getManyToManyRhsList().add(manyToManyRhs);
    assertEquals(1, manyToManyLhs.sizeManyToManyRhss());

    // Check if objects are connected in client i.e. client received update
    getClients(0).activate();
    assertEquals(1, client1ManyToManyLhs.sizeManyToManyRhss());
  }
}
