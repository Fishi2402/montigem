package associations;

import associations.test_infrastructure.AssociationsSQLDBTestSetup;
import associations.test_infrastructure.AssociationsSQLDBTestSetupTOP;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BidirectionalAssocsDbTest extends AssociationsSQLDBTestSetup {

  @DisplayName("Neo4J-BidirectionalAssocs")
  public static class
  Neo4JTestSuite extends BidirectionalAssocsDbTest {
    Neo4JTestSuite() {
      super("NEO4J");
      setUpLocalNeo4jInstance();
    }
  }

  @DisplayName("SQL-BidirectionalAssocs")
  public static class SQLTestSuite extends BidirectionalAssocsDbTest {
    SQLTestSuite() {
      super("SQL");
    }
  }

  public BidirectionalAssocsDbTest(String dbConnectionType) {
    super(0, dbConnectionType);
  }

  @Test
  public void testSettingInBuildSyncs(){
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs1 = AssociationsManager.optToManyLhsBuilder().optToManyRhs(Set.of(rhs1)).build().get();

    assertEquals(lhs1.gemId, rhs1.getOptToManyLhsGem().getGemId());
    assertTrue(lhs1.containsOptToManyRhs(rhs1));

    // ensure db
    var daoRhs = AssociationsDAOFactory.getOptToManyRhsDAO();
    var cloneRhs = daoRhs.selectOptToManyRhs(rhs1.gemId).get();
    assertNotSame(cloneRhs, rhs1);

    var daoLhs = AssociationsDAOFactory.getOptToManyLhsDAO();
    var cloneLhs = daoLhs.selectOptToManyLhs(lhs1.gemId).get();
    assertNotSame(cloneLhs, lhs1);

    assertEquals(cloneLhs.gemId, cloneRhs.getOptToManyLhsGem().getGemId());
    assertTrue(cloneLhs.containsOptToManyRhs(cloneRhs));
  }


  @Test
  public void testSettingAfterBuildSyncs(){
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs1 = AssociationsManager.optToManyLhsBuilder().build().get();

    // change left side
    lhs1.addOptToManyRhs(rhs1);

    assertEquals(lhs1.gemId, rhs1.getOptToManyLhsGem().getGemId());
    assertTrue(lhs1.containsOptToManyRhs(rhs1));

    // ensure db
    var daoRhs = AssociationsDAOFactory.getOptToManyRhsDAO();
    var cloneRhs = daoRhs.selectOptToManyRhs(rhs1.gemId).get();
    assertNotSame(cloneRhs, rhs1);

    var daoLhs = AssociationsDAOFactory.getOptToManyLhsDAO();
    var cloneLhs = daoLhs.selectOptToManyLhs(lhs1.gemId).get();
    assertNotSame(cloneLhs, lhs1);

    assertEquals(cloneLhs.gemId, cloneRhs.getOptToManyLhsGem().getGemId());
    assertTrue(cloneLhs.containsOptToManyRhs(cloneRhs));
  }

  @Test
  public void testSettingAfterBuild2Syncs(){
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs1 = AssociationsManager.optToManyLhsBuilder().build().get();

    // change right side
    rhs1.setOptToManyLhs(lhs1);

    assertEquals(lhs1.gemId, rhs1.getOptToManyLhsGem().getGemId());
    assertTrue(lhs1.containsOptToManyRhs(rhs1));

    // ensure db
    var daoRhs = AssociationsDAOFactory.getOptToManyRhsDAO();
    var cloneRhs = daoRhs.selectOptToManyRhs(rhs1.gemId).get();
    assertNotSame(cloneRhs, rhs1);

    var daoLhs = AssociationsDAOFactory.getOptToManyLhsDAO();
    var cloneLhs = daoLhs.selectOptToManyLhs(lhs1.gemId).get();
    assertNotSame(cloneLhs, lhs1);

    assertEquals(cloneLhs.gemId, cloneRhs.getOptToManyLhsGem().getGemId());
    assertTrue(cloneLhs.containsOptToManyRhs(cloneRhs));
  }

  @Test
  public void testDeleteOptToManySyncs(){
    OptToManyRhs rhs1 = AssociationsManager.optToManyRhsBuilder().build().get();
    OptToManyLhs lhs1 = AssociationsManager.optToManyLhsBuilder().optToManyRhs(Set.of(rhs1)).build().get();

    // ensure db
    var daoRhs = AssociationsDAOFactory.getOptToManyRhsDAO();
    var cloneRhs = daoRhs.selectOptToManyRhs(rhs1.gemId).get();
    assertNotSame(cloneRhs, rhs1);

    var daoLhs = AssociationsDAOFactory.getOptToManyLhsDAO();
    var cloneLhs = daoLhs.selectOptToManyLhs(lhs1.gemId).get();
    assertNotSame(cloneLhs, lhs1);

    assertEquals(cloneLhs.gemId, cloneRhs.getOptToManyLhsGem().getGemId());
    assertTrue(cloneLhs.containsOptToManyRhs(cloneRhs));

    // delete in client
    rhs1.setOptToManyLhsAbsent();

    // ensure db
    cloneRhs = daoRhs.selectOptToManyRhs(rhs1.gemId).get();
    cloneLhs = daoLhs.selectOptToManyLhs(lhs1.gemId).get();

    assertFalse(cloneRhs.getOptToManyLhsGem().isPresent());
    assertEquals(0, cloneLhs.sizeOptToManyRhss());
  }

  @Test
  public void testDeleteOptToOptSyncs(){
    OptToOptRhs rhs1 = AssociationsManager.optToOptRhsBuilder().build().get();
    OptToOptLhs lhs1 = AssociationsManager.optToOptLhsBuilder().optToOptRhs(rhs1).build().get();

    // ensure db
    var daoRhs = AssociationsDAOFactory.getOptToOptRhsDAO();
    var cloneRhs = daoRhs.selectOptToOptRhs(rhs1.gemId).get();
    assertNotSame(cloneRhs, rhs1);

    var daoLhs = AssociationsDAOFactory.getOptToOptLhsDAO();
    var cloneLhs = daoLhs.selectOptToOptLhs(lhs1.gemId).get();
    assertNotSame(cloneLhs, lhs1);

    assertEquals(cloneLhs.gemId, cloneRhs.getOptToOptLhsGem().getGemId());
    assertEquals(cloneRhs.gemId, cloneLhs.getOptToOptRhsGem().getGemId());

    // delete in client
    lhs1.setOptToOptRhsAbsent();

    // ensure the db syncs correctly
    cloneRhs = daoRhs.selectOptToOptRhs(rhs1.gemId).get();
    cloneLhs = daoLhs.selectOptToOptLhs(lhs1.gemId).get();

    assertFalse(cloneRhs.getOptToOptLhsGem().isPresent());
    assertFalse(cloneLhs.getOptToOptRhsGem().isPresent());
  }

  @Test
  public void testDeleteOptToOneSyncs(){
    OptToOneRhs rhs1 = AssociationsManager.optToOneRhsBuilder().build().get();
    OptToOneLhs lhs1 = AssociationsManager.optToOneLhsBuilder().optToOneRhs(rhs1).build().get();

    // ensure db
    var daoRhs = AssociationsDAOFactory.getOptToOneRhsDAO();
    var cloneRhs = daoRhs.selectOptToOneRhs(rhs1.gemId).get();
    assertNotSame(cloneRhs, rhs1);

    var daoLhs = AssociationsDAOFactory.getOptToOneLhsDAO();
    var cloneLhs = daoLhs.selectOptToOneLhs(lhs1.gemId).get();
    assertNotSame(cloneLhs, lhs1);

    assertEquals(cloneLhs.gemId, cloneRhs.getOptToOneLhsGem().getGemId());
    assertEquals(cloneRhs.gemId, cloneLhs.getOptToOneRhsGem().getGemId());

    // change in client
    OptToOneRhs rhs2 = AssociationsManager.optToOneRhsBuilder().build().get();
    lhs1.setOptToOneRhs(rhs2);

    // ensure the db syncs correctly
    var cloneRhs2 = AssociationsManager.getOptToOneRhsSure(rhs2.gemId);
    cloneRhs = daoRhs.selectOptToOneRhs(rhs1.gemId).get();
    cloneLhs = daoLhs.selectOptToOneLhs(lhs1.gemId).get();

    assertEquals(cloneLhs.gemId, cloneRhs2.getOptToOneLhsGem().getGemId());
    assertEquals(cloneRhs2.gemId, cloneLhs.getOptToOneRhsGem().getGemId());
    assertFalse(cloneRhs.getOptToOneLhsGem().isPresent());
  }

  @Test
  public void testDeleteManyToManySyncs(){
    ManyToManyRhs rhs1 = AssociationsManager.manyToManyRhsBuilder().build().get();
    ManyToManyLhs lhs1 = AssociationsManager.manyToManyLhsBuilder().manyToManyRhs(Set.of(rhs1)).build().get();

    // ensure db
    var daoRhs = AssociationsDAOFactory.getManyToManyRhsDAO();
    var cloneRhs = daoRhs.selectManyToManyRhs(rhs1.gemId).get();
    assertNotSame(cloneRhs, rhs1);

    var daoLhs = AssociationsDAOFactory.getManyToManyLhsDAO();
    var cloneLhs = daoLhs.selectManyToManyLhs(lhs1.gemId).get();
    assertNotSame(cloneLhs, lhs1);

    assertTrue(cloneRhs.containsManyToManyLhs(cloneLhs));
    assertTrue(cloneLhs.containsManyToManyRhs(cloneRhs));

    // delete in client
    lhs1.removeManyToManyRhs(rhs1);

    // ensure the db syncs correctly
    cloneRhs = daoRhs.selectManyToManyRhs(rhs1.gemId).get();
    cloneLhs = daoLhs.selectManyToManyLhs(lhs1.gemId).get();
    assertEquals(0, cloneRhs.sizeManyToManyLhss());
    assertEquals(0, cloneLhs.sizeManyToManyRhss());
  }

}
