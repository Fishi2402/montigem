package associations2;

import associations2.test_infrastructure.Associations2DBTestSetup;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateOptionalTest extends Associations2DBTestSetup {

  public UpdateOptionalTest() {
    super(0);
  }

  @Test
  public void testUpdateExistingOptionalInDb(){
    FleftToRight f1 = Associations2Manager.fleftToRightBuilder().build().get();
    FleftToRight f2 = Associations2Manager.fleftToRightBuilder().build().get();

    EleftToRight e = Associations2Manager.eleftToRightBuilder().fleftToRight(f1).build().get();

    var dao = new AssocEleftToRight_eleftToRight_fleftToRight_FleftToRightDAO();

    long fIdFromDb1 = dao.selectFleftToRightIdForEleftToRight(e.getGemId()).get();
    assertEquals(f1.getGemId(), fIdFromDb1);

    e.setFleftToRight(f2);

    long fIdFromDb2 = dao.selectFleftToRightIdForEleftToRight(e.getGemId()).get();
    assertEquals(f2.getGemId(), fIdFromDb2);
  }

  @Test
  public void testUpdateEmptyOptionalInDb(){
    FleftToRight f1 = Associations2Manager.fleftToRightBuilder().build().get();

    EleftToRight e = Associations2Manager.eleftToRightBuilder().build().get();

    var dao = new AssocEleftToRight_eleftToRight_fleftToRight_FleftToRightDAO();

    Optional<Long> res = dao.selectFleftToRightIdForEleftToRight(e.getGemId());
    assertTrue(res.isEmpty());

    e.setFleftToRight(f1);

    long fIdFromDb = dao.selectFleftToRightIdForEleftToRight(e.getGemId()).get();
    assertEquals(f1.getGemId(), fIdFromDb);
  }
}
