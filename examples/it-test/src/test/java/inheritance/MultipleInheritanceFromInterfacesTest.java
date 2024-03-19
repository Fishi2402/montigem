package inheritance;

import inheritance.test_infrastructure.InheritanceDBTestSetup;
import inheritance.test_infrastructure.InheritanceMockTestSetup;
import org.junit.jupiter.api.Test;
import umlp.backendrte.test_infrastructure.TestSetup;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface MultipleInheritanceFromInterfacesTest extends TestSetup<InheritanceTestContext> {

  class MultipleInheritanceFromInterfacesDBTest extends InheritanceDBTestSetup implements MultipleInheritanceFromInterfacesTest {
    public MultipleInheritanceFromInterfacesDBTest() {
      super(0);
    }
  }

  class MultipleInheritanceFromInterfacesMockTest extends InheritanceMockTestSetup  implements MultipleInheritanceFromInterfacesTest {
    public MultipleInheritanceFromInterfacesMockTest() {
      super(0);
    }
  }

  @Test
  default void testFields(){
    AssocHelper ah1 = InheritanceManager.assocHelperBuilder().build().get();
    AssocHelper ah2 = InheritanceManager.assocHelperBuilder().build().get();
    AssocHelper ah3 = InheritanceManager.assocHelperBuilder().build().get();
    AssocHelper ah4 = InheritanceManager.assocHelperBuilder().build().get();
    AssocHelper ah5 = InheritanceManager.assocHelperBuilder().build().get();
    AssocHelper ah6 = InheritanceManager.assocHelperBuilder().build().get();

    MultipleInhClass inst = InheritanceManager.multipleInhClassBuilder()
        .simpleAttr1(1)
        .simpleAttr2(2)
        .simpleAttr3(3)

        .listAttr1(List.of(4))
        .listAttr2(List.of(5))
        .listAttr3(List.of(6))

        .gemAssoc1(ah1)
        .gemAssoc2(ah2)
        .gemAssoc3(ah3)

        .listAssoc1GemListIds(List.of(ah4.gemId))
        .listAssoc2GemListIds(List.of(ah5.gemId))
        .listAssoc3GemListIds(List.of(ah6.gemId))

        .build().get();

    assertNotNull(inst);

    if(hasDatabase()){
      MultipleInhClass dbInst = InheritanceDAOFactory.getMultipleInhClassDAO().selectMultipleInhClass(inst.gemId).get();
      assertNotSame(inst, dbInst);
      assertTrue(inst.TESTING_deepEquals(dbInst));
    }
  }
}
