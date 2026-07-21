package gui.test;

import def.dom.HTMLInputElement;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static def.dom.Globals.console;
import static def.dom.Globals.document;

@Component
public class TestUtilArrays extends TestUtilArraysTOP {
  private void setResultFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("resultIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private String intArrayAsString(int[] array) {
    ArrayList<String> strings = new ArrayList<String>();
    for (int value : array) {
      strings.add(Integer.toString(value));
    }
    return stringsAsString(strings);
  }

  private String integerArrayAsString(Integer[] array) {
    ArrayList<String> strings = new ArrayList<String>();
    for (Integer value : array) {
      strings.add(Integer.toString(value));
    }
    return stringsAsString(strings);
  }

  private String integerListAsString(List<Integer> list) {
    ArrayList<String> strings = new ArrayList<String>();
    for (int value : list) {
      strings.add(Integer.toString(value));
    }
    return stringsAsString(strings);
  }

  private String stringsAsString(List<String> strings) {
    return "[" + String.join(",", strings) + "]";
  }

  // --- TESTS ---

  @Override
  public Consumer<Void> testAsListArr() {
    return (Void) -> {
      Integer[] array = new Integer[] { 1, 2, 3 };
      List<Integer> list = Arrays.asList(array);

      Object arrayAsObj = array;
      Object listAsObj = list;
      boolean notEqual = (arrayAsObj != listAsObj);

      setResultFieldEntry("{ " +
          "notEqual: " + notEqual + ", " +
          "array: " + integerArrayAsString(array) + ", " +
          "list: " + integerListAsString(list) + " }");
    };
  }

  @Override
  public Consumer<Void> testAsListParams() {
    return (Void) -> {
      List<Integer> list = Arrays.asList(1, 2, 3);
      setResultFieldEntry(integerListAsString(list));
    };
  }

  public Consumer<Void> testCopyOf() {
    return (Void) -> {
      int[] array = new int[] { 6, 5, 4, 3, 2, 1 };
      int[] copy = Arrays.copyOf(array, 3);

      setResultFieldEntry("{ " +
          "array: " + intArrayAsString(array) + ", " +
          "copy: " + intArrayAsString(copy) + " }");
    };
  }

  public Consumer<Void> testCopyOfRange() {
    return (Void) -> {
      int[] array = new int[] { 6, 5, 4, 3, 2, 1 };
      int[] copy = Arrays.copyOfRange(array, 1, 3);

      setResultFieldEntry("{ " +
          "array: " + intArrayAsString(array) + ", " +
          "copy: " + intArrayAsString(copy) + " }");
    };
  }

  public Consumer<Void> testFill() {
    return (Void) -> {
      int[] array = new int[] { 6, 5, 4, 3, 2, 1 };
      Arrays.fill(array, 7);

      setResultFieldEntry(intArrayAsString(array));
    };
  }

  public Consumer<Void> testFillRange() {
    return (Void) -> {
      int[] array = new int[] { 6, 5, 4, 3, 2, 1 };
      Arrays.fill(array, 1, 3, 7);

      setResultFieldEntry(intArrayAsString(array));
    };
  }

  public Consumer<Void> testSort() {
    return (Void) -> {
      int[] array = new int[] { 6, 5, 4, 3, 2, 1 };
      Arrays.sort(array);

      setResultFieldEntry(intArrayAsString(array));
    };
  }

  public Consumer<Void> testStream() {
    return (Void) -> {
      int[] array = new int[] { 6, 5, 4, 3, 2, 1 };
      int[] squared = Arrays.stream(array).map(v -> v * v).toArray();
    };
  }

  public Consumer<Void> testStreamRange() {
    return (Void) -> {
      int[] array = new int[] { 6, 5, 4, 3, 2, 1 };
      int[] squared = Arrays.stream(array, 1, 3).map(v -> v * v).toArray();
    };
  }

  public Consumer<Void> testToString() {
    return (Void) -> {
      String str = Arrays.toString(new Integer[] { 1, 2, null, 3 });

      setResultFieldEntry(str);
    };
  }
}
