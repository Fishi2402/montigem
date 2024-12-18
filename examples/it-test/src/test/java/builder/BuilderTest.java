package builder;

import builder.test_infrastructure.BuilderSQLDBTestSetup;
import org.junit.jupiter.api.Test;
import umlp.test_rte.ClientSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuilderTest extends BuilderSQLDBTestSetup {

  public BuilderTest() {
    super(ClientSetup.of(1));
  }

  @Test
  void testGemAssociationBuild() {
    var mainResidenceBuilder = BuilderManager.addressBuilder();
    var person = BuilderManager.personBuilder().name("Alice").mainResidence(mainResidenceBuilder).build().orElseThrow();

    assertEquals("Alice", person.getName());

    var mainResidence = person.getMainResidence();
    assertEquals("Main Street", mainResidence.getStreetName());
  }
}
