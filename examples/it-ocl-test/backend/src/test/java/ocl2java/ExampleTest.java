package ocl2java;

import org.junit.jupiter.api.Tag;

import mydomain.MydomainManager;
import mydomain.Person;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface ExampleTest extends IOCL2XTest {

  class ExampleCypherTest extends AbstractOCL2CypherTest implements ExampleTest {
  }

  @Tag("JavaGen") class ExampleJavaGenTest extends AbstractOCL2JavaGenTest<Person> implements ExampleTest {
    @Override
    protected List<Person> getContextObjects() {
      return MydomainManager.getPersonList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "SimpleConstraint"), mydomain.example.SimpleConstraint::checkInv0);
    }
  }

  String RESOURCE_FOLDER = "example";

  @Test
  default void testSimpleConstraint() throws IOException, SQLException {
    String constraint = getOclFromFile(RESOURCE_FOLDER, "SimpleConstraint");

    // Setup data
    Person alice = MydomainManager.personBuilderFromId(1000).age(21).name("Alice").build().get();
    Person error = MydomainManager.personBuilderFromId(1001).age(-21).name("Error").build().get();

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertNotEquals(alice.getGemId(), violatingObjectGemIds.get(0));
    assertEquals(error.getGemId(), violatingObjectGemIds.get(0));
  }
}
