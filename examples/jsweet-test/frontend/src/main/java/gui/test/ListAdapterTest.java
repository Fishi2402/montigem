package gui.test;

import def.dom.HTMLInputElement;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static def.dom.Globals.document;

@Component
public class ListAdapterTest extends ListAdapterTestTOP {

  private final List<String> DEFAULT_ITEMS;

  public ListAdapterTest(){
    DEFAULT_ITEMS = Arrays.asList("alpha", "beta", "gamma", "delta");
  }

  private void setResultFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("listResultIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private void setInputFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("listInputTestFieldIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private String getRawInput() {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("listInputTestFieldIdInput");
    if (el != null && el.value != null && !el.value.trim().isEmpty()) {
      return el.value.trim();
    }
    return null;
  }

  /**
   * Always returns a fresh, mutable {@link ArrayList}, so each button exercises the
   * List methods on an isolated list, independent of click order.
   */
  private List<String> getInputStrings() {
    String raw = getRawInput();
    List<String> result = new ArrayList<>();
    if (raw == null) {
      setInputFieldEntry(String.join(", ", DEFAULT_ITEMS));
      result.addAll(DEFAULT_ITEMS);
      return result;
    }
    for (String part : raw.split(",")) {
      result.add(part.trim());
    }
    return result;
  }

  public Consumer<Void> testAdd() {
    return (Void) -> {
      List<String> list = new ArrayList<>();
      list.add("alpha");
      list.add("beta");
      setResultFieldEntry("add(): size=" + list.size() + ", " + String.join(", ", list));
    };
  }

  public Consumer<Void> testGet() {
    return (Void) -> {
      List<String> list = getInputStrings();
      String got = list.get(2);
      setResultFieldEntry("get(2): " + got);
    };
  }

  public Consumer<Void> testContains() {
    return (Void) -> {
      List<String> list = getInputStrings();
      boolean hasAlpha = list.contains("alpha");
      boolean hasZeta = list.contains("zeta");
      setResultFieldEntry("contains(alpha)=" + hasAlpha + ", contains(zeta)=" + hasZeta);
    };
  }

  public Consumer<Void> testIndexOf() {
    return (Void) -> {
      List<String> list = getInputStrings();
      int idx = list.indexOf("gamma");
      setResultFieldEntry("indexOf(gamma): " + idx);
    };
  }

  public Consumer<Void> testLastIndexOf() {
    return (Void) -> {
      List<String> list = getInputStrings();
      list.add("alpha");
      int last = list.lastIndexOf("alpha");
      setResultFieldEntry("lastIndexOf(alpha): " + last);
    };
  }

  public Consumer<Void> testSet() {
    return (Void) -> {
      List<String> list = getInputStrings();
      String old = list.set(1, "delta");
      setResultFieldEntry("set(1,delta): old=" + old + ", list=" + String.join(", ", list));
    };
  }

  public Consumer<Void> testRemoveByIndex() {
    return (Void) -> {
      List<String> list = getInputStrings();
      String removed = list.remove(1);
      setResultFieldEntry("remove(1): removed=" + removed + ", list=" + String.join(", ", list));
    };
  }

  public Consumer<Void> testRemoveByObject() {
    return (Void) -> {
      List<String> list = getInputStrings();
      boolean removed = list.remove("gamma");
      setResultFieldEntry("remove(gamma): removed=" + removed + ", list=" + String.join(", ", list));
    };
  }

  public Consumer<Void> testAddAtIndex() {
    return (Void) -> {
      List<String> list = getInputStrings();
      list.add(1, "inserted");
      setResultFieldEntry("add(1,inserted): " + String.join(", ", list));
    };
  }

  public Consumer<Void> testIsEmptyAndClear() {
    return (Void) -> {
      List<String> list = new ArrayList<>();
      boolean emptyBefore = list.isEmpty();
      list.add("alpha");
      boolean emptyAfterAdd = list.isEmpty();
      list.clear();
      boolean emptyAfterClear = list.isEmpty();
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
