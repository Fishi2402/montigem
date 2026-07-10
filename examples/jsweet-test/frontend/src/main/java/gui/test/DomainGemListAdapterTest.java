package gui.test;

import def.dom.HTMLInputElement;
import jsweettest.C1;
import jsweettest.C2;
import jsweettest.JsweettestManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static def.dom.Globals.document;

@Component
public class DomainGemListAdapterTest extends DomainGemListAdapterTestTOP {

  private void setResultFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("domainGemListResultIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private void setInputFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("domainGemListInputTestFieldIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private String getRawInput() {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("domainGemListInputTestFieldIdInput");
    if (el != null && el.value != null && !el.value.trim().isEmpty()) {
      return el.value.trim();
    }
    return null;
  }

  private String getMarker() {
    String marker = getRawInput();
    if (marker == null) {
      marker = "domain-gemlist";
      setInputFieldEntry(marker);
    }
    return marker;
  }

  private List<C2> getDomainObjects() {
    final String prefix = getMarker() + ":";
    return JsweettestManager.getC2List().stream()
        .filter(c2 -> c2.getString() != null && c2.getString().startsWith(prefix))
        .collect(Collectors.toList());
  }

  private String names(List<C2> items) {
    return items.stream()
        .map(C2::getString)
        .collect(Collectors.joining(", "));
  }

  /**
   * A fresh C1 has an empty c2mult association list, which lets each button
   * exercise the GemList/List methods on an isolated list, independent of click order.
   */
  private C1 newC1() {
    return JsweettestManager.c1Builder()
        .string(getMarker())
        .int1(0)
        .int2(0)
        .hwGemId(-1L)
        .c2Req(getDomainObjects().get(0))
        .build().get();
  }

  public Consumer<Void> testAdd() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      C1 c1 = newC1();
      c1.addC2mult(items.get(0));
      c1.addC2mult(items.get(1));
      List<C2> list = c1.getC2multList();
      setResultFieldEntry("add(): size=" + list.size() + ", " + names(list));
    };
  }

  public Consumer<Void> testGet() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      C1 c1 = newC1();
      for (C2 item : items) {
        c1.addC2mult(item);
      }
      C2 got = c1.getC2multList().get(2);
      setResultFieldEntry("get(2): " + got.getString());
    };
  }

  public Consumer<Void> testContains() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      C1 c1 = newC1();
      c1.addC2mult(items.get(0));
      c1.addC2mult(items.get(1));
      boolean hasAlpha = c1.getC2multList().contains(items.get(0));
      boolean hasGamma = c1.getC2multList().contains(items.get(2));
      setResultFieldEntry("contains(alpha)=" + hasAlpha + ", contains(gamma)=" + hasGamma);
    };
  }

  public Consumer<Void> testIndexOf() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      C1 c1 = newC1();
      for (C2 item : items) {
        c1.addC2mult(item);
      }
      int idx = c1.getC2multList().indexOf(items.get(2));
      setResultFieldEntry("indexOf(gamma): " + idx);
    };
  }

  public Consumer<Void> testLastIndexOf() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      C1 c1 = newC1();
      c1.addC2mult(items.get(0));
      c1.addC2mult(items.get(1));
      c1.addC2mult(items.get(0));
      int last = c1.getC2multList().lastIndexOf(items.get(0));
      setResultFieldEntry("lastIndexOf(alpha): " + last);
    };
  }

  public Consumer<Void> testSet() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      C1 c1 = newC1();
      c1.addC2mult(items.get(0));
      c1.addC2mult(items.get(1));
      c1.addC2mult(items.get(2));
      C2 old = c1.getC2multList().set(1, items.get(3));
      List<C2> list = c1.getC2multList();
      setResultFieldEntry("set(1,delta): old=" + old.getString() + ", list=" + names(list));
    };
  }

  public Consumer<Void> testRemoveByIndex() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      C1 c1 = newC1();
      for (C2 item : items) {
        c1.addC2mult(item);
      }
      C2 removed = c1.getC2multList().remove(1);
      List<C2> list = c1.getC2multList();
      setResultFieldEntry("remove(1): removed=" + removed.getString() + ", list=" + names(list));
    };
  }

  public Consumer<Void> testRemoveByObject() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      C1 c1 = newC1();
      for (C2 item : items) {
        c1.addC2mult(item);
      }
      boolean removed = c1.getC2multList().remove(items.get(2));
      List<C2> list = c1.getC2multList();
      setResultFieldEntry("remove(gamma): removed=" + removed + ", list=" + names(list));
    };
  }

  public Consumer<Void> testAddAtIndex() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      C1 c1 = newC1();
      c1.addC2mult(items.get(0));
      c1.addC2mult(items.get(2));
      c1.getC2multList().add(1, items.get(1));
      List<C2> list = c1.getC2multList();
      setResultFieldEntry("add(1,beta): " + names(list));
    };
  }

  public Consumer<Void> testIsEmptyAndClear() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      C1 c1 = newC1();
      boolean emptyBefore = c1.getC2multList().isEmpty();
      c1.addC2mult(items.get(0));
      boolean emptyAfterAdd = c1.getC2multList().isEmpty();
      c1.getC2multList().clear();
      boolean emptyAfterClear = c1.getC2multList().isEmpty();
      setResultFieldEntry("isEmptyClear: before=" + emptyBefore + ", afterAdd=" + emptyAfterAdd + ", afterClear=" + emptyAfterClear);
    };
  }

  public Consumer<Void> testResetFields() {
    return (Void) -> {
      setInputFieldEntry("");
      setResultFieldEntry("");
    };
  }
}
