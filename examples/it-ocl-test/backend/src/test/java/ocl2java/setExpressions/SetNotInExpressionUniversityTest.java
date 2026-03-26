package ocl2java.setExpressions;

import org.junit.jupiter.api.Tag;

import mydomain.Group;
import mydomain.MydomainManager;
import mydomain.Student;
import mydomain.University;
import org.junit.jupiter.api.Test;
import setexpressions.BooleanLiteralCheck;
import setexpressions.ComplexMultiLevelCheck;
import setexpressions.GivenSet;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public interface SetNotInExpressionUniversityTest extends IOCL2XTest {
  @org.junit.jupiter.api.Disabled
  @Tag("Cypher") class SetNotInExpressionTestCypherTest extends AbstractOCL2CypherTest implements SetNotInExpressionUniversityTest {
  }

  @Tag("JavaGen") class SetNotInExpressionUniversityJavaGenTest extends AbstractOCL2JavaGenTest<University> implements SetNotInExpressionUniversityTest {
    @Override
    protected List<University> getContextObjects() {
      return MydomainManager.getUniversityList();
    }

    @Override
    protected void _initConstraints() {
      constraints.put(getOclFromFile(RESOURCE_FOLDER, "ComplexMultiLevelCheck"), ComplexMultiLevelCheck::checkInv0);
    }
  }

  String RESOURCE_FOLDER = "setexpressions";

  @Test
  default void testMultiLevelPropertyAccess() throws IOException, SQLException {
    String constraint = getOclFromFile("setexpressions", "ComplexMultiLevelCheck");

    // Create university
    University validUniv = MydomainManager.universityBuilder().name("ValidUniv").build().get();
    University invalidUniv = MydomainManager.universityBuilder().name("InvalidUniv").build().get();

    // Create students
    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").isActive(true).build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").isActive(true).build().get();
    Student charlie = MydomainManager.studentBuilder().matrNr(3).age(23).name("Charlie").isActive(false).build().get();

    // Create groups
    Group studyGroup = MydomainManager.groupBuilder().name("StudyGroup").build().get();
    Group socialGroup = MydomainManager.groupBuilder().name("SocialGroup").build().get();

    // Set up relationships
    validUniv.addStudents(charlie);
    invalidUniv.addStudents(alice);
    invalidUniv.addStudents(bob);

    alice.addGroup(studyGroup);
    bob.addGroup(socialGroup);
    charlie.addGroup(socialGroup);

    List<Long> violatingObjectGemIds = translateAndExecQuery(constraint);
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidUniv.getGemId()),
        "University with students in StudyGroup should be constrained");
    assertFalse(violatingObjectGemIds.contains(validUniv.getGemId()),
        "University without students in StudyGroup should satisfy constraint");
  }
}