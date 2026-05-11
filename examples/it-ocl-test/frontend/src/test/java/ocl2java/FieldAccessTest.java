package ocl2java;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.FieldAccess;
import mydomain.IntNumber;
import mydomain.MydomainManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface FieldAccessTest extends IOCL2XTest<FieldAccess> {
  @Tag("Cypher")
  class FieldAccessTestCypherTest extends AbstractOCL2CypherTest<FieldAccess> implements FieldAccessTest {
  }

  @Tag("JavaGen")
  class FieldAccessJavaGenTest extends AbstractOCL2JavaGenTest<FieldAccess> implements FieldAccessTest {
    @Override
    protected List<FieldAccess> getContextObjects() {
      return MydomainManager.getFieldAccessList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class FieldAccessPlaywrightTest extends AbstractOCL2JavaE2ETest<FieldAccess> implements FieldAccessTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<FieldAccess>> getConstraintToChecker() {
    Map<String, Predicate<FieldAccess>> res = new HashMap<>();
    res.put("SimpleFieldAccess", mydomain.fieldaccess.SimpleFieldAccess::checkInv0);
    res.put("ListFieldAccess", mydomain.fieldaccess.ListFieldAccess::checkInv0);
    res.put("DomainFieldAccess", mydomain.fieldaccess.DomainFieldAccess::checkInv0);
    res.put("DomainListFieldAccess", mydomain.fieldaccess.DomainListFieldAccess::checkInv0);
    res.put("SimpleAssocFieldAccess", mydomain.fieldaccess.SimpleAssocFieldAccess::checkInv0);
    res.put("AssocListFieldAccess", mydomain.fieldaccess.AssocListFieldAccess::checkInv0);
    return res;
  }

  default String getResourceFolder() {
    return "fieldaccess";
  }

  // Data
  class Data {
    IntNumber one;
    IntNumber two;
    IntNumber three;
    IntNumber negOne;
    IntNumber oneAssoc;
    IntNumber twoAssoc;
    IntNumber threeAssoc;
    IntNumber negOneAssoc;
    FieldAccess testFieldAccess;
    FieldAccess errorFieldAccess;
  }

  Data data = new Data();

  @BeforeEach
  default void dataInit() {
    data.one = MydomainManager.intNumberBuilder().value(1).build().get();
    data.two = MydomainManager.intNumberBuilder().value(2).build().get();
    data.three = MydomainManager.intNumberBuilder().value(3).build().get();
    data.negOne = MydomainManager.intNumberBuilder().value(-1).build().get();
    data.oneAssoc = MydomainManager.intNumberBuilder().value(1).build().get();
    data.twoAssoc = MydomainManager.intNumberBuilder().value(2).build().get();
    data.threeAssoc = MydomainManager.intNumberBuilder().value(3).build().get();
    data.negOneAssoc = MydomainManager.intNumberBuilder().value(-1).build().get();
    data.testFieldAccess = MydomainManager.fieldAccessBuilder()
        .name("test FA")
        .intList(Arrays.asList(1, 2, 3))
        .iNum(data.one)
        .iNumListAdd(data.one)
        .iNumListAdd(data.two)
        .iNumListAdd(data.three)
        .iNumAssoc(data.oneAssoc)
        .iNumAssocListAdd(data.oneAssoc)
        .iNumAssocListAdd(data.twoAssoc)
        .iNumAssocListAdd(data.threeAssoc)
        .build().get();
    data.errorFieldAccess = MydomainManager.fieldAccessBuilder()
        .name("error FA")
        .intList(Arrays.asList(4, 5, 6))
        .iNum(data.negOne)
        .iNumListAdd(data.two)
        .iNumListAdd(data.three)
        .iNumAssoc(data.negOneAssoc)
        .iNumAssocListAdd(data.twoAssoc)
        .iNumAssocListAdd(data.threeAssoc)
        .build().get();
  }

  @Test
  default void testSimpleFieldAccess() throws IOException, SQLException {
    String constraintName = "SimpleFieldAccess";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(data.testFieldAccess.getGemId()));
    assertTrue(violatingObjectGemIds.contains(data.errorFieldAccess.getGemId()));
  }

  @Test
  default void testListFieldAccess() throws IOException, SQLException {
    String constraintName = "ListFieldAccess";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(data.testFieldAccess.getGemId()));
    assertTrue(violatingObjectGemIds.contains(data.errorFieldAccess.getGemId()));
  }

  @Test
  default void testSimpleDomainFieldAccess() throws IOException, SQLException {
    String constraintName = "DomainFieldAccess";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(data.testFieldAccess.getGemId()));
    assertTrue(violatingObjectGemIds.contains(data.errorFieldAccess.getGemId()));
  }

  @Test
  default void testListDomainFieldAccess() throws IOException, SQLException {
    String constraintName = "DomainListFieldAccess";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(data.testFieldAccess.getGemId()));
    assertTrue(violatingObjectGemIds.contains(data.errorFieldAccess.getGemId()));
  }

  @Test
  default void testSimpleAssocFieldAccess() throws IOException, SQLException {
    String constraintName = "SimpleAssocFieldAccess";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(data.testFieldAccess.getGemId()));
    assertTrue(violatingObjectGemIds.contains(data.errorFieldAccess.getGemId()));
  }

  @Test
  default void testAssocListFieldAccess() throws IOException, SQLException {
    String constraintName = "AssocListFieldAccess";

    // Exec query
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);

    // check results
    assertEquals(1, violatingObjectGemIds.size());
    assertFalse(violatingObjectGemIds.contains(data.testFieldAccess.getGemId()));
    assertTrue(violatingObjectGemIds.contains(data.errorFieldAccess.getGemId()));
  }

}
