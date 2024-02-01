/* (c) https://github.com/MontiCore/monticore */
package attributes;

import attributes.test_infrastructure.AttributesDBTestSetup;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class DbTypesTest extends AttributesDBTestSetup {
  public DbTypesTest() {
    super(0);
  }

  @Test
  public void testListOfPrimitivesIsWrittenToDb() {
    List<Integer> values = List.of(0, 0, 1, 2, 3, 4);
    var sut = AttributesManager.listOfPrimitivesBuilder().ints(values).build().get();

    var dao = AttributesDAOFactory.getListOfPrimitivesDAO();

    var clone = dao.selectListOfPrimitives(sut.gemId).get();
    assertEquals(values, clone.getIntsList());
  }

  @Test
  public void testAddToListOfPrimitivesInDb() {
    List<Integer> values = List.of(0, -1, -2, -3, -4);
    ListOfPrimitives sut = AttributesManager.listOfPrimitivesBuilder().ints(values).build().get();
    var dao = AttributesDAOFactory.getListOfPrimitivesDAO();
    var clone = dao.selectListOfPrimitives(sut.gemId).get();
    assertEquals(5, clone.getIntsList().size());

    // add element at the end
    sut.addInts(5, -5);
    assertEquals(6, sut.getIntsList().size());
    clone = dao.selectListOfPrimitives(sut.gemId).get();
    assertEquals(6, clone.getIntsList().size());

    // add element in the middle
    sut.addInts(1, 11);
    assertEquals(7, sut.getIntsList().size());
    assertEquals(11, sut.getInts(1));
    clone = dao.selectListOfPrimitives(sut.gemId).get();
    assertEquals(7, clone.getIntsList().size());
    assertEquals(11, clone.getInts(1));
  }

  @Test
  public void testDeleteInListOfPrimitivesInDb() {
    List<Integer> values = List.of(-1, -1, -2, -3, -4);
    var sut = AttributesManager.listOfPrimitivesBuilder().ints(values).build().get();

    var dao = AttributesDAOFactory.getListOfPrimitivesDAO();
    var clone = dao.selectListOfPrimitives(sut.gemId).get();
    assertEquals(5, clone.getIntsList().size());

    // delete at the beginning
    sut.removeInts(0);
    assertEquals(4, sut.getIntsList().size());
    clone = dao.selectListOfPrimitives(sut.gemId).get();
    assertEquals(4, clone.getIntsList().size());

    // delete at the end
    sut.removeInts(3);
    assertEquals(3, sut.getIntsList().size());
    clone = dao.selectListOfPrimitives(sut.gemId).get();
    assertEquals(3, clone.getIntsList().size());
  }

  @Test
  public void testListOfStringsIsWrittenToDb() {
    List<String> values = List.of("A", "B", "C");
    var sut = AttributesManager.listOfStringsBuilder().strings(values).build().get();

    var dao = AttributesDAOFactory.getListOfStringsDAO();

    var clone = dao.selectListOfStrings(sut.gemId).get();
    assertEquals(values, clone.getStringsList());
  }

  @Test
  public void testAddToListOfStringsInDb() {
    List<String> values = List.of("A", "B", "C");
    var sut = AttributesManager.listOfStringsBuilder().strings(values).build().get();
    var dao = AttributesDAOFactory.getListOfStringsDAO();
    var clone = dao.selectListOfStrings(sut.gemId).get();
    assertEquals(3, clone.getStringsList().size());

    // add element at the end
    sut.addStrings(3, "X");
    assertEquals(4, sut.getStringsList().size());
    clone = dao.selectListOfStrings(sut.gemId).get();
    assertEquals(4, clone.getStringsList().size());

    // add element in the middle
    sut.addStrings(1, "Y");
    assertEquals(5, sut.getStringsList().size());
    assertEquals("Y", sut.getStrings(1));
    clone = dao.selectListOfStrings(sut.gemId).get();
    assertEquals(5, clone.getStringsList().size());
    assertEquals("Y", clone.getStrings(1));
  }

  @Test
  public void testDeleteInListOfStringsInDb() {
    List<String> values = List.of("A", "B", "C");
    var sut = AttributesManager.listOfStringsBuilder().strings(values).build().get();
    var dao = AttributesDAOFactory.getListOfStringsDAO();
    var clone = dao.selectListOfStrings(sut.gemId).get();
    assertEquals(3, clone.getStringsList().size());

    // delete at the beginning
    sut.removeStrings(0);
    assertEquals(2, sut.getStringsList().size());
    clone = dao.selectListOfStrings(sut.gemId).get();
    assertEquals(2, clone.getStringsList().size());

    // delete at the end
    sut.removeStrings(1);
    assertEquals(1, sut.getStringsList().size());
    clone = dao.selectListOfStrings(sut.gemId).get();
    assertEquals(1, clone.getStringsList().size());
  }

  @Test
  public void testOptionalOfStringIsWritten() {
    Optional<String> value = Optional.of("A");
    var sut = AttributesManager.optionalOfStringBuilder().str(value).build().get();

    // db
    var dao = AttributesDAOFactory.getOptionalOfStringDAO();
    var clone = dao.selectOptionalOfString(sut.gemId).get();
    assertNotSame(sut, clone);

    assertTrue(clone.getStr().isPresent());
    assertEquals("A", clone.getStr().get());
  }

  @Test
  public void testSetToOptionalOfString() {
    var sut = AttributesManager.optionalOfStringBuilder().build().get();

    // db
    var dao = AttributesDAOFactory.getOptionalOfStringDAO();
    var clone = dao.selectOptionalOfString(sut.gemId).get();
    assertNotSame(sut, clone);
    assertFalse(clone.getStr().isPresent());

    sut.setStr(Optional.of("A"));
    clone = dao.selectOptionalOfString(sut.gemId).get();
    assertTrue(sut.getStr().isPresent());
    assertTrue(clone.getStr().isPresent());
  }

  @Test
  public void testDeleteInOptionalOfString() {
    Optional<String> value = Optional.of("A");
    var sut = AttributesManager.optionalOfStringBuilder().str(value).build().get();

    // db
    var dao = AttributesDAOFactory.getOptionalOfStringDAO();
    var clone = dao.selectOptionalOfString(sut.gemId).get();
    assertNotSame(sut, clone);
    assertTrue(clone.getStr().isPresent());

    sut.setStr(Optional.empty());
    clone = dao.selectOptionalOfString(sut.gemId).get();
    assertFalse(sut.getStr().isPresent());
    assertFalse(clone.getStr().isPresent());
  }
}
