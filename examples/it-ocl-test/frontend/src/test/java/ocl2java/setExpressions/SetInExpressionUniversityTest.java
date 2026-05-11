package ocl2java.setExpressions;

import com.microsoft.playwright.junit.UsePlaywright;
import mydomain.Group;
import mydomain.MydomainManager;
import mydomain.Student;
import mydomain.University;
import mydomain.setexpressions.MultiLevelPropertyAccess;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import util.AbstractOCL2CypherTest;
import util.AbstractOCL2JavaE2ETest;
import util.AbstractOCL2JavaGenTest;
import util.IOCL2XTest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
public interface SetInExpressionUniversityTest extends IOCL2XTest<University> {
  @Tag("Cypher")
  class SetInExpressionTestCypherTest extends AbstractOCL2CypherTest<University> implements SetInExpressionUniversityTest {
  }

  @Tag("JavaGen")
  class SetInExpressionJavaGenTest extends AbstractOCL2JavaGenTest<University> implements SetInExpressionUniversityTest {
    @Override
    protected List<University> getContextObjects() {
      return MydomainManager.getUniversityList();
    }
  }

  @UsePlaywright
  @Tag("Playwright")
  class SetInExpressionPlaywrightTest extends AbstractOCL2JavaE2ETest<University> implements SetInExpressionUniversityTest {
    @Override
    protected void _initConstraints() {
      registerGeneratedOclPages(getConstraintToChecker().keySet());
    }
  }

  @Override
  default Map<String, Predicate<University>> getConstraintToChecker() {
    return Map.of(
        "MultiLevelPropertyAccess",
        MultiLevelPropertyAccess::checkInv0
    );
  }

  default String getResourceFolder() {
    return "setexpressions";
  }

  @Test
  default void testMultiLevelPropertyAccess() throws IOException, SQLException {
    String constraintName = "MultiLevelPropertyAccess";

    University validUniv = MydomainManager.universityBuilder().name("ValidUniv").build().get();
    University invalidUniv = MydomainManager.universityBuilder().name("InvalidUniv").build().get();

    Student alice = MydomainManager.studentBuilder().matrNr(1).age(21).name("Alice").isActive(true).build().get();
    Student bob = MydomainManager.studentBuilder().matrNr(2).age(22).name("Bob").isActive(true).build().get();
    Student charlie = MydomainManager.studentBuilder().matrNr(3).age(23).name("Charlie").isActive(false).build().get();

    Group studyGroup = MydomainManager.groupBuilder().name("StudyGroup").build().get();
    Group socialGroup = MydomainManager.groupBuilder().name("SocialGroup").build().get();

    validUniv.addStudents(alice);
    validUniv.addStudents(bob);
    invalidUniv.addStudents(charlie);

    alice.addGroup(studyGroup);
    bob.addGroup(socialGroup);
    charlie.addGroup(socialGroup);
    List<Long> violatingObjectGemIds = translateAndExecQueryFromPath(constraintName);
    assertEquals(1, violatingObjectGemIds.size());
    assertTrue(violatingObjectGemIds.contains(invalidUniv.getGemId()),
        "University without students in StudyGroup should be constrained");
    assertFalse(violatingObjectGemIds.contains(validUniv.getGemId()),
        "University with students in StudyGroup should satisfy constraint");

  }
}
