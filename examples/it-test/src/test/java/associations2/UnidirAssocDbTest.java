package associations2;

import associations2.test_infrastructure.Associations2DBTestSetup;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnidirAssocDbTest extends Associations2DBTestSetup {
  public UnidirAssocDbTest() {
    super(1);
  }

  @Test
  public void testOptIsSavedToDbInBuilder(){
    getClients(0).activate();

    var fleftToRight = Associations2Manager.fleftToRightBuilder().build().get();
    var eleftToRight = Associations2Manager.eleftToRightBuilder().fleftToRight(fleftToRight).build().get();
    assertEquals(fleftToRight.gemId, eleftToRight.getFleftToRight().gemId);

    EleftToRightDAO dao = new EleftToRightDAO();
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

    EleftToRightDAO dao = new EleftToRightDAO();
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

    CleftToRightDAO dao = new CleftToRightDAO();
    var clone = dao.selectCleftToRight(rhs.gemId).get();

    assertEquals(rhs.gemId, clone.gemId);
    assertEquals(rhs.getDleftToRight(), rhs.getDleftToRight());
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

    CleftToRightDAO dao = new CleftToRightDAO();
    var clone = dao.selectCleftToRight(rhs.gemId).get();

    assertEquals(rhs.gemId, clone.gemId);
    assertEquals(rhs.getDleftToRight(), rhs.getDleftToRight());
  }
}
