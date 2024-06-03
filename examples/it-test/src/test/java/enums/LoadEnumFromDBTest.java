package enums;

import enums.test_infrastructure.EnumsSQLDBTestSetup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadEnumFromDBTest extends EnumsSQLDBTestSetup {
  public LoadEnumFromDBTest() {
    super(0);
  }

  @Test
  public void loadEmptyEnumOptFromDB(){
    Foo original = EnumsManager.fooBuilder().cc(Colour.GREEN).build().get();
    Foo fromDb = EnumsDAOFactory.getFooDAO().selectFoo(original.getGemId()).get();

    assertTrue(original.TESTING_deepEquals(fromDb));
  }
}
