package inheritance;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.DatabaseAccessManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InheritanceManagerImplTest {

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

  @BeforeEach
  public void init() throws IOException {
    InheritanceTestContext.clear();
    InheritanceManagerImpl.init();
    DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");
    String schema = IOUtils.resourceToString("/Inheritance.sql", StandardCharsets.UTF_8);
    assertTrue(DatabaseAccessManager.initSchema(schema));
  }

  @AfterEach
  public void cleanup() throws SQLException {
    DatabaseAccessManager.closeConnection();
  }

  @Test
  public void testSubclassesOfConcreteClass(){
    A a = InheritanceManager.aBuilder().aField(A_FIELD).build().get();
    B b = InheritanceManager.bBuilder().aField(A_FIELD).bField(B_FIELD).build().get();
    D d = InheritanceManager.dBuilder().aField(A_FIELD).cField(C_FIELD).dField(D_FIELD).build().get();

    // getA
    assertEquals(a, InheritanceManager.getASure(a.getGemId()));
    assertEquals(b, InheritanceManager.getASure(b.getGemId()));
    assertEquals(d, InheritanceManager.getASure(d.getGemId()));

    // getC
    assertEquals(d, InheritanceManager.getCSure(d.getGemId()));
  }

  @Test
  public void testSubclassesOfInterface(){
    E e = InheritanceManager.eBuilder().iField(I_FIELD).eField(E_FIELD).build().get();
    G g = InheritanceManager.gBuilder().iField(I_FIELD).fField(F_FIELD).gField(G_FIELD).build().get();

    // getI
    assertEquals(e, InheritanceManager.getISure(e.getGemId()));
    assertEquals(g, InheritanceManager.getISure(g.getGemId()));

    // getF
    assertEquals(g, InheritanceManager.getFSure(g.getGemId()));
  }

  @Test
  public void testSubclassesOfSubinterface(){
    H h = InheritanceManager.hBuilder().iField(I_FIELD).jField(J_FIELD).hField(H_FIELD).build().get();
    L l = InheritanceManager.lBuilder().iField(I_FIELD).jField(J_FIELD).kField(K_FIELD).lField(L_FIELD).build().get();

    // getI
    assertEquals(h, InheritanceManager.getISure(h.getGemId()));
    assertEquals(l, InheritanceManager.getISure(l.getGemId()));

    // getJ
    assertEquals(h, InheritanceManager.getJSure(h.getGemId()));
    assertEquals(l, InheritanceManager.getJSure(l.getGemId()));

    // getK
    assertEquals(l, InheritanceManager.getKSure(l.getGemId()));
  }

  // Same tests without the cache

  @Test
  public void testSubclassesOfConcreteClassDB(){
    ABuilder aBuilder = InheritanceManager.aBuilder().aField(A_FIELD);
    BBuilder bBuilder = InheritanceManager.bBuilder().aField(A_FIELD).bField(B_FIELD);
    DBuilder dBuilder = InheritanceManager.dBuilder().aField(A_FIELD).cField(C_FIELD).dField(D_FIELD);

    // Don't add to Manager
    aBuilder.setINTERNAL_deser_mode(true);
    bBuilder.setINTERNAL_deser_mode(true);
    dBuilder.setINTERNAL_deser_mode(true);

    A a = aBuilder.build().get();
    B b = bBuilder.build().get();
    D d = dBuilder.build().get();

    assertFalse(InheritanceManager.aOpt(a.getGemId()).isPresent());
    assertFalse(InheritanceManager.bOpt(b.getGemId()).isPresent());
    assertFalse(InheritanceManager.dOpt(d.getGemId()).isPresent());

    // Save in DB
    new ADAO().insertA(a);
    new BDAO().insertB(b);
    new DDAO().insertD(d);

    // getA
    A aCopy = InheritanceManager.getASure(a.getGemId());
    A bCopy = InheritanceManager.getASure(b.getGemId());
    A dCopy = InheritanceManager.getASure(d.getGemId());

    assertTrue(bCopy instanceof B);
    assertTrue(dCopy instanceof D);

    assertEquals(a.getGemId(), aCopy.getGemId());
    assertEquals(b.getGemId(), bCopy.getGemId());
    assertEquals(d.getGemId(), dCopy.getGemId());

    assertEquals(a.getAField(), aCopy.getAField());

    assertEquals(b.getAField(), bCopy.getAField());
    assertEquals(b.getBField(), ((B) bCopy).getBField());

    assertEquals(d.getAField(), dCopy.getAField());
    assertEquals(d.getCField(), ((D) dCopy).getCField());
    assertEquals(d.getDField(), ((D) dCopy).getDField());

    // getC
    A dCopy2 = InheritanceManager.getCSure(d.getGemId());
    assertTrue(dCopy2 instanceof D);
    assertEquals(d.getGemId(), dCopy2.getGemId());
  }

  @Test
  public void testSubclassesOfInterfaceDB(){
    EBuilder eBuilder = InheritanceManager.eBuilder().iField(I_FIELD).eField(E_FIELD);
    GBuilder gBuilder = InheritanceManager.gBuilder().iField(I_FIELD).fField(F_FIELD).gField(G_FIELD);

    // Don't add to Manager
    eBuilder.setINTERNAL_deser_mode(true);
    gBuilder.setINTERNAL_deser_mode(true);

    E e = eBuilder.build().get();
    G g = gBuilder.build().get();

    assertFalse(InheritanceManager.eOpt(e.getGemId()).isPresent());
    assertFalse(InheritanceManager.gOpt(g.getGemId()).isPresent());

    // Add to DB
    new EDAO().insertE(e);
    new GDAO().insertG(g);

    // getI
    I eCopy = InheritanceManager.getISure(e.getGemId());
    I gCopy = InheritanceManager.getISure(g.getGemId());

    assertTrue(eCopy instanceof E);
    assertTrue(gCopy instanceof G);

    assertEquals(e.getGemId(), eCopy.getGemId());
    assertEquals(g.getGemId(), gCopy.getGemId());

    assertEquals(e.getIField(), ((E) eCopy).getIField());
    assertEquals(e.getEField(), ((E) eCopy).getEField());

    assertEquals(g.getIField(), ((G) gCopy).getIField());
    assertEquals(g.getFField(), ((G) gCopy).getFField());
    assertEquals(g.getGField(), ((G) gCopy).getGField());

    // getF
    F gCopy2 = InheritanceManager.getFSure(g.getGemId());
    assertTrue(gCopy2 instanceof G);
    assertEquals(g.getGemId(), gCopy2.getGemId());

    assertEquals(g.getIField(), gCopy2.getIField());
    assertEquals(g.getFField(), gCopy2.getFField());
    assertEquals(g.getGField(), ((G) gCopy2).getGField());
  }

  @Test
  public void testSubclassesOfSubinterfaceDB(){
    HBuilder hBuilder = InheritanceManager.hBuilder().iField(I_FIELD).jField(J_FIELD).hField(H_FIELD);
    LBuilder lBuilder = InheritanceManager.lBuilder().iField(I_FIELD).jField(J_FIELD).kField(K_FIELD).lField(L_FIELD);

    // Don't add to Manager
    hBuilder.setINTERNAL_deser_mode(true);
    lBuilder.setINTERNAL_deser_mode(true);

    H h = hBuilder.build().get();
    L l = lBuilder.build().get();

    assertFalse(InheritanceManager.hOpt(h.getGemId()).isPresent());
    assertFalse(InheritanceManager.lOpt(l.getGemId()).isPresent());

    // Add to DB
    new HDAO().insertH(h);
    new LDAO().insertL(l);

    // getI
    I hCopy = InheritanceManager.getISure(h.getGemId());
    I lCopy = InheritanceManager.getISure(l.getGemId());

    assertEquals(h.getGemId(), hCopy.getGemId());
    assertEquals(l.getGemId(), lCopy.getGemId());

    assertTrue(hCopy instanceof H);
    assertTrue(lCopy instanceof L);

    assertEquals(h.getIField(), hCopy.getIField());
    assertEquals(h.getJField(), ((H) hCopy).getJField());
    assertEquals(h.getHField(), ((H) hCopy).getHField());

    // getJ
    J hCopy2 = InheritanceManager.getJSure(h.getGemId());
    J lCopy2 = InheritanceManager.getJSure(l.getGemId());

    assertEquals(h.getGemId(), hCopy2.getGemId());
    assertEquals(l.getGemId(), lCopy2.getGemId());

    assertTrue(hCopy2 instanceof H);
    assertTrue(lCopy2 instanceof L);

    assertEquals(h.getIField(), hCopy2.getIField());
    assertEquals(h.getJField(), hCopy2.getJField());
    assertEquals(h.getHField(), ((H) hCopy2).getHField());

    assertEquals(l.getIField(), lCopy2.getIField());
    assertEquals(l.getJField(), lCopy2.getJField());
    assertEquals(l.getLField(), ((L) lCopy2).getLField());

    // getK
    K lCopy3 = InheritanceManager.getKSure(l.getGemId());
    assertEquals(l.getGemId(), lCopy3.getGemId());

    assertTrue(lCopy3 instanceof L);

    assertEquals(l.getLField(), ((L) lCopy3).getLField());
    assertEquals(l.getIField(), ((L) lCopy3).getIField());
    assertEquals(l.getJField(), ((L) lCopy3).getJField());
    assertEquals(l.getKField(), ((L) lCopy3).getKField());
  }

}
