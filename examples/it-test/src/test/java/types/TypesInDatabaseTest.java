/* (c) https://github.com/MontiCore/monticore */
package types;

import org.junit.jupiter.api.Test;
import types.test_infrastructure.TypesDBTestSetup;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypesInDatabaseTest extends TypesDBTestSetup {
  public TypesInDatabaseTest() {
    super(0);
  }

  @Test
  public void testFloatTypesAreWrittenToDB(){
    FloatingPoints fp = TypesManager.floatingPointsBuilder().d1(2d).d2(3d).f1(4f).f2(5f).build().get();
    FloatingPointsDAO dao = new FloatingPointsDAO();

    Optional<FloatingPoints> fpCopyOpt = dao.selectFloatingPoints(fp.gemId);

    assertTrue(fpCopyOpt.isPresent());
    FloatingPoints fpCopy = fpCopyOpt.get();
    assertNotSame(fp, fpCopy);

    assertEquals(fp.getF1(), fpCopy.getF1(), 10e-10);
    assertEquals(fp.getF2(), fpCopy.getF2(), 10e-10);
    assertEquals(fp.getD1(), fpCopy.getD1(), 10e-10);
    assertEquals(fp.getD2(), fpCopy.getD2(), 10e-10);
  }

  @Test
  public void testLongStringWrittenToDb(){
    String longStr = "a".repeat(5000);
    StringTypes s = TypesManager.stringTypesBuilder().strFieldNoStereotype(longStr).build().get();
    assertEquals(longStr, s.getStrFieldNoStereotype());

    // Check db
    StringTypes sClone = TypesDAOFactory.getStringTypesDAO().selectStringTypes(s.gemId).get();
    assertNotSame(s, sClone);

    assertEquals(longStr, sClone.getStrFieldNoStereotype());
  }
}
