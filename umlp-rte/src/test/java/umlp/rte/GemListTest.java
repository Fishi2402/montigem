// (c) https://github.com/MontiCore/monticore
package umlp.rte;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.rte.lazyloadTestUtil.Elem;
import umlp.rte.lazyloadTestUtil.ListBasedServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GemListTest extends AbstractUMLPRTETest {

  List<Elem> allElems;

  //starts empty
  ListBasedServer<Elem> listServer0;

  //starts with 4 elements
  ListBasedServer<Elem> listServer1;

  //has some elements not authorized to be accessed
  ListBasedServer<Elem> listServer2;

  GemList<Elem> list0;

  GemList<Elem> list1;

  GemList<Elem> list2;

  @BeforeEach
  public void init() {
    Elem.nextID = 0;
    allElems = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      allElems.add(new Elem());
    }
    allElems = Collections.unmodifiableList(allElems);

    listServer0 = new ListBasedServer<>(new LinkedList<>());
    listServer0.addKnownObjects(allElems);
    list0 = listServer0.createLazyList();

    listServer1 = new ListBasedServer<>(allElems.subList(0, 4));
    listServer1.addKnownObjects(allElems);
    list1 = listServer1.createLazyList();

    listServer2 = new ListBasedServer<>(allElems.subList(0, 2));
    listServer2.addKnownObjects(allElems);
    listServer2.setIdsNotAuthorized(Collections.singleton(getElem(1).getGemId()));
    list2 = listServer2.createLazyList();
  }

  @Test
  public void lazyListTestSize() {
    assertEquals(list0.size(), 0);
    assertTrue(list0.isEmpty());
    assertEquals(list1.size(), 4);
    assertFalse(list1.isEmpty());
    assertNoErrors();
  }

  @Test
  public void lazyListTestGet() {
    assertElemNr(list1.get(0), 0);
    assertElemNr(list1.get(1), 1);
    assertElemNr(list1.get(2), 2);
    assertElemNr(list1.get(3), 3);
    assertNoErrors();
  }

  @Test
  public void lazyListTestGetOutOfBoundsNegative() {
    assertThrows(IndexOutOfBoundsException.class, () -> list1.get(-3));
  }

  @Test
  public void lazyListTestGetOutOfBoundsPositive() {
    assertThrows(IndexOutOfBoundsException.class, () -> list1.get(list1.size()));
  }

  @Test
  public void lazyListTestRemove() {
    Elem old;
    old = list1.remove(2);
    assertElemNr(old, 2);
    assertEquals(list1.size(), 3);
    old = list1.remove(2);
    assertElemNr(old, 3);
    assertEquals(list1.size(), 2);
    assertElemNr(list1.get(0), 0);
    assertElemNr(list1.get(1), 1);
    list1.removeAll(allElems);
    assertTrue(list1.isEmpty());
    assertNoErrors();
  }

  @Test
  public void lazyListTestRemoveOutOfBoundsNegative() {
    assertThrows(IndexOutOfBoundsException.class, () -> list1.remove(-3));
  }

  @Test
  public void lazyListTestRemoveOutOfBoundsPositive() {
    assertThrows(IndexOutOfBoundsException.class, () -> list1.remove(list1.size()));
  }

  @Test
  public void lazyListTestAdd() {
    assertEquals(list1.size(), 4);
    list1.add(getElem(2));
    assertEquals(list1.size(), 5);
    assertElemNr(list1.get(4), 2);
    Elem elem8 = getElem(8);
    list1.add(2, elem8);
    assertEquals(list1.size(), 6);
    assertElemNr(list1.get(0), 0);
    assertElemNr(list1.get(1), 1);
    assertElemNr(list1.get(2), 8);
    assertElemNr(list1.get(3), 2);
    assertElemNr(list1.get(4), 3);
    assertElemNr(list1.get(5), 2);
    assertNoErrors();
  }

  @Test
  public void lazyListTestSet() {
    list1.set(0, getElem(4));
    list1.set(3, getElem(7));
    assertEquals(list1.size(), 4);
    assertElemNr(list1.get(0), 4);
    assertElemNr(list1.get(1), 1);
    assertElemNr(list1.get(2), 2);
    assertElemNr(list1.get(3), 7);
    assertNoErrors();
  }

  @Test
  public void lazyListTestSetOutOfBoundsNegative() {
    assertThrows(IndexOutOfBoundsException.class, () -> list1.set(-3, getElem(0)));
  }

  @Test
  public void lazyListTestSetOutOfBoundsPositive() {
    assertThrows(IndexOutOfBoundsException.class, () -> list1.set(list1.size(), getElem(0)));
  }

  @Test
  public void lazyListTestIsPresent() {
    assertEquals(list2.size(), 2);
    assertTrue(list2.isPresent(0));
    assertFalse(list2.isPresent(1));
    assertNoErrors();
  }

  @Test
  public void lazyListTestIsPresentOutOfBoundsNegative() {
    assertThrows(IndexOutOfBoundsException.class, () -> list1.isPresent(-3));
  }

  @Test
  public void lazyListTestIsPresentOutOfBoundsPositive() {
    assertThrows(IndexOutOfBoundsException.class, () -> list1.isPresent(list1.size()));
  }

  protected Elem getElem(int idx) {
    return allElems.get(idx);
  }

  protected void assertElemNr(Elem elem, int idx) {
    assertSame(allElems.get(idx), elem);
  }
}