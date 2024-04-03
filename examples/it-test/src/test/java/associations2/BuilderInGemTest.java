package associations2;

import associations2.test_infrastructure.Associations2MockTestSetup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuilderInGemTest extends Associations2MockTestSetup {

  public BuilderInGemTest() {
    super(0);
  }

  @Test
  public void testAddBuilderToGem(){
    BleftToRightBuilder b = Associations2Manager.bleftToRightBuilder();
    AleftToRightBuilder a = Associations2Manager.aleftToRightBuilder().bleftToRight(b);

    assertEquals(b, a.getBleftToRight());
  }

  @Test
  public void testAddBuilderToGemAsBuilder(){
    BleftToRightBuilder b = Associations2Manager.bleftToRightBuilder();
    AleftToRight a = Associations2Manager.aleftToRightBuilder();
    a.setBleftToRight(b);

    assertEquals(b, a.getBleftToRight());
  }
}
