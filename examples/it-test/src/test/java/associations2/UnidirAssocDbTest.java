/* (c) https://github.com/MontiCore/monticore */
package associations2;

import associations2.test_infrastructure.Associations2Neo4JDBTestSetup;
import associations2.test_infrastructure.Associations2SQLDBTestSetup;
import associations2.test_infrastructure.Associations2TestSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface UnidirAssocDbTest extends Associations2TestSetup {

  @DisplayName("Neo4J-UnidirectionalAssocs")
  public static class Neo4JTestSuite extends Associations2Neo4JDBTestSetup implements UnidirAssocDbTest {
    Neo4JTestSuite() {
      super(1);
    }
  }

  @DisplayName("SQL-UnidirectionalAssocs")
  public static class SQLTestSuite extends Associations2SQLDBTestSetup implements UnidirAssocDbTest {
    SQLTestSuite() {
      super(1);
    }
  }

  @Test
  default void testClientAssocUpdateIsSynchronized() {
    getBackend().activate();
    var cLeftToRight = Associations2Manager.cleftToRightBuilder().build().get();
    Associations2Manager.dleftToRightBuilder().build().get();

    getClients(0).activate();
    var clientCLeftToRight = Associations2Manager.getCleftToRightList().get(0);
    assertNotEquals(cLeftToRight.cleftToRightObservers, clientCLeftToRight.cleftToRightObservers);
    clientCLeftToRight.addDleftToRight(Associations2Manager.getDleftToRightList().get(0));

    getBackend().activate();
    var dao = Associations2DAOFactory.getCleftToRightDAO();
    var clone = dao.selectCleftToRight(cLeftToRight.gemId).get();

    assertEquals(clone.getDleftToRightList(), cLeftToRight.getDleftToRightList());
  }

  @Test
  default void testOptIsSavedToDbInBuilder(){
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
  default void testOptIsSavedToDbAfterBuilder(){
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
  default void testListIsSavedToDbInBuilder(){
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
  default void testListIsSavedToDbAfterBuilder(){
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
