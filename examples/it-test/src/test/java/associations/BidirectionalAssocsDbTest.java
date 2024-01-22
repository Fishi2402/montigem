package associations;

import associations.test_infrastructure.AssociationsDBTestSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BidirectionalAssocsDbTest extends AssociationsDBTestSetup {
  public BidirectionalAssocsDbTest() {
    super(0);
  }

  @Test
  public void testCreationOfBidirAssoc(){
    Log.clearFindings();
    // Situation from Python artifact model, which resulted in primary key violation:
    // Create builder for pythonView
    OneToManyLhsBuilder artifactViewBuilder = AssociationsManager.oneToManyLhsBuilder();

    // build python module
    // - directly
    // - with empty assocs
    // - artifactView = pythonView
    OneToManyRhs artifactInternal = AssociationsManager.oneToManyRhsBuilder().oneToManyLhs(artifactViewBuilder).build().get();

    // build pythonView after
    // - directly
    // - with gemid 4 in artifactinternal
    artifactViewBuilder.oneToManyRhsAdd(artifactInternal).build().get();

    assertEquals(0, Log.getErrorCount());
  }
}
