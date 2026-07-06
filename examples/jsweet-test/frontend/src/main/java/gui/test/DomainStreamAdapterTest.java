package gui.test;

import de.se_rwth.commons.logging.Log;
import def.dom.HTMLInputElement;
import jsweettest.C2;
import jsweettest.JsweettestManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static def.dom.Globals.document;

@Component
public class DomainStreamAdapterTest extends DomainStreamAdapterTestTOP {

  private void setResultFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("domainResultIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private void setInputFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("domainInputTestFieldIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private String getRawInput() {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("domainInputTestFieldIdInput");
    if (el != null && el.value != null && !el.value.trim().isEmpty()) {
      return el.value.trim();
    }
    return null;
  }

  private String getMarker() {
    String marker = getRawInput();
    if (marker == null) {
      marker = "domain-stream";
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

  private List<C2> getDomainObjectsWithDuplicate() {
    List<C2> items = getDomainObjects();
    List<C2> result = new ArrayList<>();
    if (items.size() > 0) {
      result.add(items.get(0));
    }
    if (items.size() > 1) {
      result.add(items.get(1));
      result.add(items.get(1));
    }
    if (items.size() > 2) {
      result.add(items.get(2));
      result.add(items.get(2));
    }
    return result;
  }

  private String names(List<C2> items) {
    return items.stream()
        .map(C2::getString)
        .collect(Collectors.joining(", "));
  }

  public Consumer<Void> testMap() {
    return (Void) -> {
      List<Integer> doubled = getDomainObjects().stream()
          .map(c2 -> c2.getInt1() * 2)
          .collect(Collectors.toList());
      setResultFieldEntry("map(): " + doubled.toString());
    };
  }

  public Consumer<Void> testFilter() {
    return (Void) -> {
      List<C2> evens = getDomainObjects().stream()
          .filter(c2 -> c2.getInt1() % 2 == 0)
          .collect(Collectors.toList());
      setResultFieldEntry("filter(): " + names(evens));
    };
  }

  public Consumer<Void> testMapAndFilter() {
    return (Void) -> {
      List<String> res = getDomainObjects().stream()
          .filter(c2 -> c2.getString().length() > 0)
          .map(c2 -> c2.getString().toUpperCase())
          .collect(Collectors.toList());
      setResultFieldEntry("filter()+map(): " + res.toString());
    };
  }

  public Consumer<Void> testFlatMap() {
    return (Void) -> {
      List<C2> items = getDomainObjects();
      List<List<C2>> nested = Arrays.asList(
          items.subList(0, 2),
          items.subList(2, items.size())
      );

      List<C2> flat = nested.stream()
          .flatMap(list -> list.stream())
          .collect(Collectors.toList());
      setResultFieldEntry("flatMap(): " + names(flat));
    };
  }

  public Consumer<Void> testDistinct() {
    return (Void) -> {
      List<C2> unique = getDomainObjectsWithDuplicate().stream()
          .distinct()
          .collect(Collectors.toList());
      setResultFieldEntry("distinct(): " + names(unique));
    };
  }

  public Consumer<Void> testLimit() {
    return (Void) -> {
      List<C2> limited = getDomainObjects().stream()
          .limit(3)
          .collect(Collectors.toList());
      setResultFieldEntry("limit(3): " + names(limited));
    };
  }

  public Consumer<Void> testSkip() {
    return (Void) -> {
      List<C2> skipped = getDomainObjects().stream()
          .skip(2)
          .collect(Collectors.toList());
      setResultFieldEntry("skip(2): " + names(skipped));
    };
  }

  public Consumer<Void> testSorted() {
    return (Void) -> {
      List<C2> sorted = getDomainObjects().stream()
          .sorted((a, b) -> a.getInt2() - b.getInt2())
          .collect(Collectors.toList());
      setResultFieldEntry("sorted(): " + names(sorted));
    };
  }

  public Consumer<Void> testPeek() {
    return (Void) -> {
      final StringBuilder sb = new StringBuilder();
      getDomainObjects().stream()
          .peek(c2 -> sb.append(c2.getString()).append(";"))
          .collect(Collectors.toList());
      setResultFieldEntry("peek(): " + sb.toString());
    };
  }

  public Consumer<Void> testCount() {
    return (Void) -> {
      long count = getDomainObjects().stream().filter(c2 -> c2.getInt1() > 15).count();
      setResultFieldEntry("count(>15): " + count);
    };
  }

  public Consumer<Void> testReduce() {
    return (Void) -> {
      C2 min = getDomainObjects().stream()
          .reduce((a, b) -> a.getInt1() <= b.getInt1() ? a : b)
          .orElse(null);
      setResultFieldEntry("reduce(min): " + (min == null ? "none" : min.getString()));
    };
  }

  public Consumer<Void> testToArray() {
    return (Void) -> {
      Object[] arr = getDomainObjects().stream().toArray();
      List<C2> list = new ArrayList<>();
      for (Object o : arr) {
        list.add((C2) o);
      }
      setResultFieldEntry("toArray(): " + names(list));
    };
  }

  public Consumer<Void> testFindAny() {
    return (Void) -> {
      C2 val = getDomainObjects().stream()
          .filter(c2 -> c2.getInt1() > 35)
          .findAny()
          .orElse(null);
      setResultFieldEntry("findAny(): " + (val == null ? "none" : val.getString()));
    };
  }

  public Consumer<Void> testForEach() {
    return (Void) -> {
      StringBuilder sb = new StringBuilder();
      getDomainObjects().stream().forEach(c2 -> sb.append(c2.getString()).append("!"));
      setResultFieldEntry("forEach(): " + sb.toString());
    };
  }

  public Consumer<Void> testFindFirst() {
    return (Void) -> {
      C2 val = getDomainObjects().stream()
          .filter(c2 -> c2.getInt1() > 15)
          .findFirst()
          .orElse(null);
      setResultFieldEntry("findFirst(>15): " + (val == null ? "none" : val.getString()));
    };
  }

  public Consumer<Void> testNoneMatch() {
    return (Void) -> {
      boolean none = getDomainObjects().stream().noneMatch(c2 -> c2.getInt1() > 100);
      setResultFieldEntry("noneMatch(>100): " + none);
    };
  }

  public Consumer<Void> testCollectJoining() {
    return (Void) -> {
      String res = getDomainObjects().stream()
          .map(C2::getString)
          .collect(Collectors.joining(" - "));
      setResultFieldEntry("collect(joining): " + res);
    };
  }

  public Consumer<Void> testMatches() {
    return (Void) -> {
      boolean any = getDomainObjects().stream().anyMatch(c2 -> c2.getInt1() > 20);
      boolean all = getDomainObjects().stream().allMatch(c2 -> c2.getInt1() < 50);
      setResultFieldEntry("any>20: " + any + ", all<50: " + all);
    };
  }

  public Consumer<Void> testMin() {
    return (Void) -> {
      C2 min = getDomainObjects().stream()
          .min((a, b) -> a.getInt1() - b.getInt1())
          .orElse(null);
      setResultFieldEntry("min(): " + (min == null ? "none" : min.getString()));
    };
  }

  public Consumer<Void> testMax() {
    return (Void) -> {
      C2 max = getDomainObjects().stream()
          .max((a, b) -> a.getInt1() - b.getInt1())
          .orElse(null);
      setResultFieldEntry("max(): " + (max == null ? "none" : max.getString()));
    };
  }

  public Consumer<Void> testClear() {
    return (Void) -> {
      setInputFieldEntry("");
      setResultFieldEntry("");
    };
  }

  public Consumer<Void> testCollectToSet() {
    return (Void) -> {
      Set<C2> uniqueSet = getDomainObjectsWithDuplicate().stream().collect(Collectors.toSet());

      Log.warn(uniqueSet.toString());

      List<C2> list = uniqueSet.stream()
          .sorted((a, b) -> a.getInt1() - b.getInt1())
          .collect(Collectors.toList());
      setResultFieldEntry("collect(toSet): " + names(list));
    };
  }

  public Consumer<Void> testCollectCounting() {
    return (Void) -> {
      Long count = getDomainObjects().stream().collect(Collectors.counting());
      setResultFieldEntry("collect(counting): " + count);
    };
  }
}
