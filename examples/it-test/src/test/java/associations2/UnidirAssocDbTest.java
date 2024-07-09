/* (c) https://github.com/MontiCore/monticore */
package associations2;

import associations2.test_infrastructure.Associations2SQLDBTestSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class UnidirAssocDbTest extends Associations2SQLDBTestSetup {

  public UnidirAssocDbTest(String dbConnectionType) {
    super(1, dbConnectionType);
  }

  @DisplayName("Neo4J-UnidirectionalAssocs")
  public static class
  Neo4JTestSuite extends UnidirAssocDbTest {
    Neo4JTestSuite() {
      super("NEO4J");
      setUpLocalNeo4jInstance();
    }
  }

  @DisplayName("SQL-UnidirectionalAssocs")
  public static class SQLTestSuite extends UnidirAssocDbTest {
    SQLTestSuite() {
      super("SQL");
    }
  }

  @Test
  public void testOptIsSavedToDbInBuilder(){
    getClients(0).activate();

    var fleftToRight = Associations2Manager.fleftToRightBuilder().build().get();
    var eleftToRight = Associations2Manager.eleftToRightBuilder().fleftToRight(fleftToRight).build().get();
    assertEquals(fleftToRight.gemId, eleftToRight.getFleftToRight().gemId);

    getBackend().activate();
    var dao = Associations2DAOFactory.getEleftToRightDAO();
    var clone = dao.selectEleftToRight(eleftToRight.gemId).get();

    assertEquals(eleftToRight.gemId, clone.gemId);
    assertEquals(eleftToRight.getFleftToRight().getGemId(), clone.getFleftToRight().getGemId());
  }

  @Test
  public void testOptIsSavedToDbAfterBuilder(){
    getClients(0).activate();

    var rhs = Associations2Manager.fleftToRightBuilder().build().get();
    var lhs = Associations2Manager.eleftToRightBuilder().build().get();

    lhs.setFleftToRight(rhs);

    assertEquals(rhs.gemId, lhs.getFleftToRight().gemId);

    getBackend().activate();
    var dao = Associations2DAOFactory.getEleftToRightDAO();
    var clone = dao.selectEleftToRight(lhs.gemId).get();

    assertEquals(lhs.gemId, clone.gemId);
    assertEquals(lhs.getFleftToRight().getGemId(), clone.getFleftToRight().getGemId());
  }

  @Test
  public void testListIsSavedToDbInBuilder(){
    getClients(0).activate();

    var lhs1 = Associations2Manager.dleftToRightBuilder().build().get();
    var lhs2 = Associations2Manager.dleftToRightBuilder().build().get();
    var rhs = Associations2Manager.cleftToRightBuilder().dleftToRight(Set.of(lhs1, lhs2)).build().get();

    assertTrue(rhs.containsDleftToRight(lhs1));
    assertTrue(rhs.containsDleftToRight(lhs2));

    getBackend().activate();
    var dao = Associations2DAOFactory.getCleftToRightDAO();
    var clone = dao.selectCleftToRight(rhs.gemId).get();

    assertEquals(rhs.gemId, clone.gemId);
    assertEquals(rhs.getDleftToRightList(), rhs.getDleftToRightList());
  }

  @Test
  public void testListIsSavedToDbAfterBuilder(){
    getClients(0).activate();

    var lhs1 = Associations2Manager.dleftToRightBuilder().build().get();
    var lhs2 = Associations2Manager.dleftToRightBuilder().build().get();
    var rhs = Associations2Manager.cleftToRightBuilder().build().get();

    rhs.addAllDleftToRights(Set.of(lhs1, lhs2));

    assertTrue(rhs.containsDleftToRight(lhs1));
    assertTrue(rhs.containsDleftToRight(lhs2));

    getBackend().activate();
    var dao = Associations2DAOFactory.getCleftToRightDAO();
    var clone = dao.selectCleftToRight(rhs.gemId).get();

    assertEquals(rhs.gemId, clone.gemId);
    assertEquals(rhs.getDleftToRightList(), rhs.getDleftToRightList());
  }
}
