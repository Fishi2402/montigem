package inheritance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InheritanceManagerMockTest {

  @Test
  public void testSubclassesOfConcreteClass(){
    InheritanceMock.init();

    A a = InheritanceManager.aBuilder().build().get();
    B b = InheritanceManager.bBuilder().build().get();
    D d = InheritanceManager.dBuilder().build().get();

    // getA
    assertEquals(a, InheritanceManager.getASure(a.gemId));
    assertEquals(b, InheritanceManager.getASure(b.gemId));
    assertEquals(d, InheritanceManager.getASure(d.gemId));

    // getC
    assertEquals(d, InheritanceManager.getCSure(d.gemId));
  }

  @Test
  public void testSubclassesOfInterface(){
    InheritanceMock.init();

    E e = InheritanceManager.eBuilder().build().get();
    G g = InheritanceManager.gBuilder().build().get();

    // getI
    assertEquals(e, InheritanceManager.getISure(e.gemId));
    assertEquals(g, InheritanceManager.getISure(g.gemId));

    // getF
    assertEquals(g, InheritanceManager.getFSure(g.gemId));
  }

  @Test
  public void testSubclassesOfSubinterface(){
    InheritanceMock.init();

    H h = InheritanceManager.hBuilder().build().get();
    L l = InheritanceManager.lBuilder().build().get();

    // getI
    assertEquals(h, InheritanceManager.getISure(h.gemId));
    assertEquals(l, InheritanceManager.getISure(l.gemId));

    // getJ
    assertEquals(h, InheritanceManager.getJSure(h.gemId));
    assertEquals(l, InheritanceManager.getJSure(l.gemId));

    // getK
    assertEquals(l, InheritanceManager.getKSure(l.gemId));
  }


}
