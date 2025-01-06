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
    assertTrue(mainResidenceBuilder.build().isEmpty()); //should not be able to be build
    //should also not be able to build, if the assoc can't build
    assertTrue(BuilderManager.personBuilder().name("Alice").mainResidence(mainResidenceBuilder).build().isEmpty());
  }

  @Test
  void testGemAssociationBuildWithCompleteBuilder() {
    var mainResidenceBuilder = BuilderManager.addressBuilder().streetName("Main Street");
    assertTrue(mainResidenceBuilder.build().isPresent());
    var alice = BuilderManager.personBuilder().name("Alice").mainResidence(mainResidenceBuilder).build().orElseThrow();
    assertEquals("Alice", alice.getName());
    assertEquals("Main Street", alice.getMainResidence().getStreetName());
  }
}
