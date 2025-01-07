package builder;

import builder.test_infrastructure.BuilderSQLDBTestSetup;
import org.junit.jupiter.api.Test;
import umlp.test_rte.ClientSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuilderTest extends BuilderSQLDBTestSetup {

  public BuilderTest() {
    super(ClientSetup.of(1));
  }

  @Test
  void testGemAssociationBuildWithIncompleteBuilder() {
    var mainResidenceBuilder = BuilderManager.addressBuilder();
    //should not be able to build, since residence builder should not be able to build
    assertTrue(BuilderManager.personBuilder().name("Alice").mainResidence(mainResidenceBuilder).build().isEmpty());
  }

  @Test
  void testGemAssociationBuildWithCompleteBuilder() {
    var mainResidenceBuilder = BuilderManager.addressBuilder().streetName("Main Street");
    var alice = BuilderManager.personBuilder().name("Alice").mainResidence(mainResidenceBuilder).build().orElseThrow();
    assertTrue(mainResidenceBuilder.isBuildFinished());
    assertEquals("Alice", alice.getName());
    assertEquals("Main Street", alice.getMainResidence().getStreetName());
  }
}
