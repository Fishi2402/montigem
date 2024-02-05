/* (c) https://github.com/MontiCore/monticore */
package inheritance;

import de.se_rwth.commons.logging.Log;
import inheritance.test_infrastructure.InheritanceMockTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InheritanceManagerSyncTest extends InheritanceMockTestSetup {

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

  public InheritanceManagerSyncTest() {
    super(1);
  }

 @BeforeEach
  public void setup() {
    Log.init();
  }

  @Test
  public void testSubclassesOfConcreteClass(){
    // client
    getClients(0).activate();
    A a = InheritanceManager.aBuilder().aField(A_FIELD).build().get();
    B b = InheritanceManager.bBuilder().aField(A_FIELD).bField(B_FIELD).build().get();
    D d = InheritanceManager.dBuilder().aField(A_FIELD).cField(C_FIELD).dField(D_FIELD).build().get();

    // getA
    assertEquals(a, InheritanceManager.getASure(a.gemId));
    assertEquals(b, InheritanceManager.getASure(b.gemId));
    assertEquals(d, InheritanceManager.getASure(d.gemId));

    // getC
    assertEquals(d, InheritanceManager.getCSure(d.gemId));

    // ensure the server syncs correctly
    getBackend().activate();

    A aServer = InheritanceManager.getASure(a.gemId);
    B bServer = InheritanceManager.getBSure(b.gemId);
    D dServer = InheritanceManager.getDSure(d.gemId);

    // getA
    assertEquals(aServer, InheritanceManager.getASure(a.gemId));
    assertEquals(bServer, InheritanceManager.getASure(b.gemId));
    assertEquals(dServer, InheritanceManager.getASure(d.gemId));

    // getC
    assertEquals(dServer, InheritanceManager.getCSure(d.gemId));

    // checkAttributes
    assertEquals(A_FIELD, aServer.getAField());

    assertEquals(A_FIELD, bServer.getAField());
    assertEquals(B_FIELD, bServer.getBField());

    assertEquals(A_FIELD, dServer.getAField());
    assertEquals(C_FIELD, dServer.getCField());
    assertEquals(D_FIELD, dServer.getDField());

  }

  @Test
  public void testSubclassesOfInterface(){
    // client
    getClients(0).activate();

    E e = InheritanceManager.eBuilder().iField(I_FIELD).eField(E_FIELD).build().get();
    G g = InheritanceManager.gBuilder().iField(I_FIELD).fField(F_FIELD).gField(G_FIELD).build().get();

    // getI
    assertEquals(e, InheritanceManager.getISure(e.gemId));
    assertEquals(g, InheritanceManager.getISure(g.gemId));

    // getF
    assertEquals(g, InheritanceManager.getFSure(g.gemId));

    // ensure the server syncs correctly
    getBackend().activate();
    E eServer = InheritanceManager.getESure(e.gemId);
    G gServer = InheritanceManager.getGSure(g.gemId);

    // getI
    assertEquals(eServer, InheritanceManager.getISure(e.gemId));
    assertEquals(gServer, InheritanceManager.getISure(g.gemId));

    // getF
    assertEquals(gServer, InheritanceManager.getFSure(g.gemId));

    // checkAttributes
    assertEquals(E_FIELD, eServer.getEField());
    assertEquals(I_FIELD, eServer.getIField());

    assertEquals(I_FIELD, gServer.getIField());
    assertEquals(F_FIELD, gServer.getFField());
    assertEquals(G_FIELD, gServer.getGField());

  }

  @Test
  public void testSubclassesOfSubinterface(){
    // client
    getClients(0).activate();

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

    // ensure the server syncs correctly
    getBackend().activate();
    H hServer = InheritanceManager.getHSure(h.gemId);
    L lServer = InheritanceManager.getLSure(l.gemId);

    // getI
    assertEquals(hServer, InheritanceManager.getISure(h.gemId));
    assertEquals(lServer, InheritanceManager.getISure(l.gemId));

    // getJ
    assertEquals(hServer, InheritanceManager.getJSure(h.gemId));
    assertEquals(lServer, InheritanceManager.getJSure(l.gemId));

    // getK
    assertEquals(lServer, InheritanceManager.getKSure(l.gemId));

    // checkAttributes
    assertEquals(I_FIELD, hServer.getIField());
    assertEquals(J_FIELD, hServer.getJField());
    assertEquals(H_FIELD, hServer.getHField());

    assertEquals(I_FIELD, lServer.getIField());
    assertEquals(J_FIELD, lServer.getJField());
    assertEquals(K_FIELD, lServer.getKField());
    assertEquals(L_FIELD, lServer.getLField());

  }

  @Test
  public void testDeleteSubclassesOfConcreteClass(){
    // client
    getClients(0).activate();
    D d = InheritanceManager.dBuilder().aField(A_FIELD).cField(C_FIELD).dField(D_FIELD).build().get();

    // getA
    assertEquals(d, InheritanceManager.getASure(d.gemId));

    // getC
    assertEquals(d, InheritanceManager.getCSure(d.gemId));

    // ensure the server syncs correctly
    getBackend().activate();
    D dServer = InheritanceManager.getDSure(d.gemId);

    // getA
    assertEquals(dServer, InheritanceManager.getASure(d.gemId));

    // getC
    assertEquals(dServer, InheritanceManager.getCSure(d.gemId));

    // client
    getClients(0).activate();
    InheritanceManager.deleteD(d.gemId);

    // getA
    assertFalse(InheritanceManager.isLoadedA(d.gemId));

    // getC
    assertFalse(InheritanceManager.isLoadedC(d.gemId));

    // ensure the server syncs correctly
    getBackend().activate();

    // getA
    assertFalse(InheritanceManager.isLoadedA(d.gemId));

    // getC
    assertFalse(InheritanceManager.isLoadedC(d.gemId));

  }


}
