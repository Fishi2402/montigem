package ocl2java;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.MydomainManager;
import mydomain.Person;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@UsePlaywright
public interface ExampleTest extends IOCL2XTest<Person> {

  class ExampleCypherTest extends AbstractOCL2CypherTest<Person> implements ExampleTest {
  }

  @Tag("JavaGen")
  class ExampleJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements ExampleTest {
    @Override
    protected List<Person> getContextObjects() {
      return MydomainManager.getPersonList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class ExamplePlaywrightTest extends AbstractOCL2JavaE2ETest<Person> implements ExampleTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<Person>> getConstraintToChecker() {
    Map<String, Predicate<Person>> res = new HashMap<>();
    res.put("SimpleConstraint", mydomain.example.SimpleConstraint::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "example";
  }

  @Test
  default void testSimpleConstraint() throws IOException, SQLException {
    String constraintName = "SimpleConstraint";

    // Setup data
    Person alice = MydomainManager.personBuilderFromId(1000).age(21).name("Alice").build().get();
    Person error = MydomainManager.personBuilderFromId(1001).age(-21).name("Error").build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertNotEquals(alice.getGemId(), violatingObjectGemIds.get(0));
    assertEquals(error.getGemId(), violatingObjectGemIds.get(0));
  }
}
