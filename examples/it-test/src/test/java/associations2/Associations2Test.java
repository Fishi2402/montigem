/* (c) https://github.com/MontiCore/monticore */
package associations2;

import associations.AssociationsManager;
import associations.AssociationsMock;
import associations.ManyToManyLhs;
import associations.ManyToManyRhs;
import associations.ManyToOneLhs;
import associations.ManyToOneRhs;
import associations.ManyToOptLhs;
import associations.ManyToOptRhs;
import associations.ManyToSomeLhs;
import associations.ManyToSomeRhs;
import associations.OneToManyLhs;
import associations.OneToManyRhs;
import associations.OneToOptLhs;
import associations.OneToOptRhs;
import associations.OptToManyLhs;
import associations.OptToManyRhs;
import associations.OptToOneLhs;
import associations.OptToOneRhs;
import associations.OptToOptLhs;
import associations.OptToOptRhs;
import associations.OptToSomeLhs;
import associations.OptToSomeRhs;
import associations.SomeToManyLhs;
import associations.SomeToManyRhs;
import associations.SomeToOptLhs;
import associations.SomeToOptRhs;
import associations2.test_infrastructure.Associations2MockTestSetup;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Associations2Test extends Associations2MockTestSetup {

  public Associations2Test() {
    super(1);
  }

  @Test
  public void testLeftToRightDeleteRight() {
    Associations2Mock.init();

    DleftToRight rs = Associations2Manager.dleftToRightBuilder().build().get();
    CleftToRight ls = Associations2Manager.cleftToRightBuilder().dleftToRightAdd(rs).build().get();
    assertTrue(ls.getDleftToRight().contains(rs));

    Associations2Manager.deleteDleftToRight(rs.getGemId());
    assertFalse(Associations2Manager.isLoadedDleftToRight(rs.gemId));
    // TODO
    // assertTrue(ls.getDleftToRight().isEmpty());
  }

  @Test
  public void testLeftToRightDeleteLeft() {
    Associations2Mock.init();

    GleftToRight ls = Associations2Manager.gleftToRightBuilder().build().get();
    HleftToRight rs = Associations2Manager.hleftToRightBuilder().build().get();
    ls.addHleftToRight(rs);
    assertTrue(ls.getHleftToRight().contains(rs));

    Associations2Manager.deleteHleftToRight(rs.getGemId());
    // TODO
    // assertTrue(ls.getHleftToRight().isEmpty());
  }

  @Test
  public void testDeleteSync() {
    Associations2Mock.init();
    getClients(0).activate();

    GleftToRight ls = Associations2Manager.gleftToRightBuilder().build().get();

    getBackend().activate();

    Associations2Manager.deleteGleftToRight(ls.getGemId());
    // TODO
    // assertTrue(ls.getHleftToRight().isEmpty());
  }
}
