package associations;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BidirectionalAssociationsTest {

  // TODO: Impl setting the other side
  @Disabled
  @Test
  public void testOptToOptSync(){
    AssociationsMock.init();

    OptToOptLhs lhs = AssociationsManager.optToOptLhsBuilder().build().get();
    OptToOptRhs rhs = AssociationsManager.optToOptRhsBuilder().build().get();

    lhs.setOptToOptRhs(rhs);
    assertEquals(lhs, rhs.getOptToOptLhs());
  }

}
