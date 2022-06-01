// (c) https://github.com/MontiCore/monticore
package umlp.rte;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.rte.lazyloadTestUtil.ListBasedServer;
import umlp.rte.lazyloadTestUtil.Elem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GemListTest extends AbstractUMLPRTETest {

  List<Elem> allElems;

  //starts empty
  ListBasedServer<Elem> listServer0;

  //starts with 4 elements
  ListBasedServer<Elem> listServer1;

  //has elements not authorized
  ListBasedServer<Elem> listServer2;

  GemList<Elem> list0;

  GemList<Elem> list1_0;

  GemList<Elem> list1_1;

  GemList<Elem> list2;

  @BeforeEach
  public void init() {
    allElems = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      allElems.add(new Elem());
    }

    listServer0 = new ListBasedServer<>(new LinkedList<>());
    listServer0.addKnownObjects(allElems);
    list0 = listServer0.createLazyList();

    listServer1 = new ListBasedServer<>(allElems.subList(0, 4));
    listServer1.addKnownObjects(allElems);
    list1_0 = listServer1.createLazyList();
    list1_1 = listServer1.createLazyList();

    listServer2 = new ListBasedServer<>(allElems.subList(0, 2));
    listServer2.addKnownObjects(allElems);
    listServer2.setIdsNotAuthorized(Collections.singleton(getElem(1).getGemId()));
    list2 = listServer2.createLazyList();
  }

  @Test
  public void lazyListTestSize() {
    assertEquals(list0.size(), 0);
    assertTrue(list0.isEmpty());
    assertEquals(list1_0.size(), 4);
    assertEquals(list1_1.size(), 4);
    assertNoErrors();
  }

  @Test
  public void lazyListTestGet() {
    assertElemNr(list1_0.get(0), 0);
    assertElemNr(list1_0.get(1), 1);
    assertElemNr(list1_0.get(2), 2);
    assertElemNr(list1_0.get(3), 3);
    assertNoErrors();
  }

  @Test
  public void lazyListTestRemove() {
    Elem old;
    old = list1_0.remove(3);
    assertElemNr(old, 3);
    assertEquals(list1_0.size(), 3);
    assertEquals(list1_1.size(), 3);
    old = list1_1.remove(2);
    assertElemNr(old, 2);
    assertEquals(list1_0.size(), 2);
    assertEquals(list1_1.size(), 2);
    list1_0.removeAll(allElems);
    assertTrue(list1_0.isEmpty());
    assertNoErrors();
  }

  @Test
  public void lazyListTestAdd() {
    assertEquals(list1_0.size(), 4);
    list1_0.add(getElem(2));
    assertEquals(list1_0.size(), 5);
    assertEquals(list1_1.size(), 5);
    assertElemNr(list1_0.get(4), 2);
    assertElemNr(list1_1.get(4), 2);
    list1_0.add(2, getElem(8));
    assertEquals(list1_0.size(), 6);
    assertElemNr(list1_0.get(0), 0);
    assertElemNr(list1_0.get(1), 1);
    assertElemNr(list1_0.get(2), 8);
    assertElemNr(list1_0.get(3), 2);
    assertElemNr(list1_0.get(4), 3);
    assertElemNr(list1_0.get(5), 2);
    assertNoErrors();
  }

  @Test
  public void lazyListTestSet() {
    list1_0.set(0, getElem(4));
    list1_0.set(3, getElem(7));
    assertEquals(list1_1.size(), 4);
    assertElemNr(list1_1.get(0), 4);
    assertElemNr(list1_1.get(1), 1);
    assertElemNr(list1_1.get(2), 2);
    assertElemNr(list1_1.get(3), 7);
    assertNoErrors();
  }

  @Test
  public void lazyListTestIsPresent() {
    assertEquals(list2.size(), 2);
    assertTrue(list2.isPresent(0));
    assertFalse(list2.isPresent(1));
    assertNoErrors();
  }

  protected Elem getElem(int idx) {
    return allElems.get(idx);
  }

  protected void assertElemNr(Elem elem, int idx) {
    assertSame(allElems.get(idx), elem);
  }
}