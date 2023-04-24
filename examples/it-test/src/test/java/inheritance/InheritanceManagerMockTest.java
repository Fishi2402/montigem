package inheritance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InheritanceManagerMockTest {

  public static final int A_FIELD = 1;
  public static final int B_FIELD = 2;
  public static final int C_FIELD = 3;
  public static final int D_FIELD = 4;
  public static final int E_FIELD = 5;
  public static final int F_FIELD = 6;
  public static final int G_FIELD = 7;
  public static final int H_FIELD = 8;
  public static final int I_FIELD = 9;
  public static final int J_FIELD = 10;
  public static final int K_FIELD = 11;
  public static final int L_FIELD = 12;

  @Test
  public void testSubclassesOfConcreteClass(){
    InheritanceMock.init();

    A a = InheritanceManager.aBuilder().aField(A_FIELD).build().get();
    B b = InheritanceManager.bBuilder().aField(A_FIELD).bField(B_FIELD).build().get();
    D d = InheritanceManager.dBuilder().aField(A_FIELD).cField(C_FIELD).dField(D_FIELD).build().get();

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

    E e = InheritanceManager.eBuilder().iField(I_FIELD).eField(E_FIELD).build().get();
    G g = InheritanceManager.gBuilder().iField(I_FIELD).fField(F_FIELD).gField(G_FIELD).build().get();

    // getI
    assertEquals(e, InheritanceManager.getISure(e.gemId));
    assertEquals(g, InheritanceManager.getISure(g.gemId));

    // getF
    assertEquals(g, InheritanceManager.getFSure(g.gemId));
  }

  @Test
  public void testSubclassesOfSubinterface(){
    InheritanceMock.init();

    H h = InheritanceManager.hBuilder().iField(I_FIELD).jField(J_FIELD).hField(H_FIELD).build().get();
    L l = InheritanceManager.lBuilder().iField(I_FIELD).jField(J_FIELD).kField(K_FIELD).lField(L_FIELD).build().get();

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
