package gui.test;

import def.dom.HTMLInputElement;
import umlp.jsweet.extension.annotation.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static def.dom.Globals.document;

@Component
public class ArrayStreamAdapterTest extends ArrayStreamAdapterTestTOP {

  private void setResultFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("resultIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private void setInputFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("inputTestFieldIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private String getRawInput() {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("inputTestFieldIdInput");
    if (el != null && el.value != null && !el.value.trim().isEmpty()) {
      return el.value.trim();
    }
    return null;
  }

  private List<Integer> getInputIntegers(List<Integer> fallback) {
    String raw = getRawInput();
    if (raw == null) {
      String defaultStr = fallback.stream()
          .map(o -> String.valueOf(o))
          .collect(Collectors.joining(", "));
      setInputFieldEntry(defaultStr);
      return fallback;
    }
    try {
      return Stream.of(raw.split(","))
          .map(String::trim)
          .map(Integer::parseInt)
          .collect(Collectors.toList());
    } catch (Exception e) {
      return fallback;
    }
  }

  private List<String> getInputStrings(List<String> fallback) {
    String raw = getRawInput();
    if (raw == null) {
      String defaultStr = String.join(", ", fallback);
      setInputFieldEntry(defaultStr);
      return fallback;
    }
    return Stream.of(raw.split(","))
        .map(String::trim)
        .collect(Collectors.toList());
  }

  private List<List<String>> getInputNestedStrings(List<List<String>> fallback) {
    String raw = getRawInput();
    if (raw == null) {
      String defaultStr = fallback.stream()
          .map(list -> String.join(", ", list))
          .collect(Collectors.joining("; "));
      setInputFieldEntry(defaultStr);
      return fallback;
    }
    List<List<String>> result = new ArrayList<>();
    String[] groups = raw.split(";");
    for (String group : groups) {
      result.add(Stream.of(group.split(","))
          .map(String::trim)
          .collect(Collectors.toList()));
    }
    return result;
  }

  // --- TESTS ---

  @Override
  public Consumer<Void> testMap() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3, 4));
      List<Integer> doubled = nums.stream()
          .map(x -> x * 2)
          .collect(Collectors.toList());
      setResultFieldEntry("map(): " + doubled.toString());
    };
  }

  public Consumer<Void> testFilter() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3, 4, 5, 6));
      List<Integer> evens = nums.stream()
          .filter(n -> n % 2 == 0)
          .collect(Collectors.toList());
      setResultFieldEntry("filter(): " + evens.toString());
    };
  }

  public Consumer<Void> testMapAndFilter() {
    return (Void) -> {
      List<String> items = getInputStrings(Arrays.asList("a", "bb", "ccc", "dddd"));
      List<String> res = items.stream()
          .filter(s -> s.length() > 2)
          .map(String::toUpperCase)
          .collect(Collectors.toList());
      setResultFieldEntry("filter()+map(): " + res.toString());
    };
  }

  public Consumer<Void> testFlatMap() {
    return (Void) -> {
      List<List<String>> fallback = Arrays.asList(
          Arrays.asList("a", "b"),
          Arrays.asList("c", "d")
      );
      List<List<String>> nested = getInputNestedStrings(fallback);

      List<String> flat = nested.stream()
          .flatMap(l -> l.stream())
          .collect(Collectors.toList());
      setResultFieldEntry("flatMap(): " + flat.toString());
    };
  }

  public Consumer<Void> testDistinct() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 2, 3, 3, 3));
      List<Integer> unique = nums.stream()
          .distinct()
          .collect(Collectors.toList());
      setResultFieldEntry("distinct(): " + unique.toString());
    };
  }

  public Consumer<Void> testLimit() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3, 4, 5));
      List<Integer> limited = nums.stream()
          .limit(3)
          .collect(Collectors.toList());
      setResultFieldEntry("limit(3): " + limited.toString());
    };
  }

  public Consumer<Void> testSkip() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3, 4, 5));
      List<Integer> skipped = nums.stream()
          .skip(2)
          .collect(Collectors.toList());
      setResultFieldEntry("skip(2): " + skipped.toString());
    };
  }

  public Consumer<Void> testSorted() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(5, 1, 4, 2, 3));
      List<Integer> sorted = nums.stream()
          .sorted()
          .collect(Collectors.toList());
      setResultFieldEntry("sorted(): " + sorted.toString());
    };
  }

  public Consumer<Void> testPeek() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3));
      final StringBuilder sb = new StringBuilder();
      nums.stream()
          .peek(x -> sb.append(x).append(";"))
          .collect(Collectors.toList());
      setResultFieldEntry("peek(): " + sb.toString());
    };
  }

  public Consumer<Void> testCount() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 10, 20));
      long count = nums.stream().filter(n -> n > 5).count();
      setResultFieldEntry("count(>5): " + count);
    };
  }

  public Consumer<Void> testReduce() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3, 4));
      int sum = nums.stream().reduce(0, (a, b) -> a + b);
      setResultFieldEntry("reduce(sum): " + sum);
    };
  }

  public Consumer<Void> testToArray() {
    return (Void) -> {
      List<String> items = getInputStrings(Arrays.asList("one", "two", "three"));
      Object[] arr = items.stream().toArray();
      List<String> list = new ArrayList<>();
      for (Object o : arr) {
        list.add(String.valueOf(o));
      }
      String joined = String.join(", ", list);
      setResultFieldEntry("toArray(): [" + joined + "]");
    };
  }

  public Consumer<Void> testFindAny() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 10));
      int val = nums.stream().filter(n -> n > 5).findAny().orElse(-1);
      setResultFieldEntry("findAny(): " + val);
    };
  }

  public Consumer<Void> testForEach() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3));
      StringBuilder sb = new StringBuilder();
      nums.stream().forEach(n -> sb.append(n).append("!"));
      setResultFieldEntry("forEach(): " + sb.toString());
    };
  }

  public Consumer<Void> testFindFirst() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3, 99, 100, 101));
      int val = nums.stream().filter(n -> n > 5).findFirst().orElse(-1);
      setResultFieldEntry("findFirst(>5): " + val);
    };
  }

  public Consumer<Void> testNoneMatch() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3));
      boolean none = nums.stream().noneMatch(n -> n > 10);
      setResultFieldEntry("noneMatch(>10): " + none);
    };
  }

  public Consumer<Void> testCollectJoining() {
    return (Void) -> {
      List<String> items = getInputStrings(Arrays.asList("Apple", "Banana", "Cherry"));
      String res = items.stream().collect(Collectors.joining(" - "));
      setResultFieldEntry("collect(joining): " + res);
    };
  }

  public Consumer<Void> testMatches() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3));
      boolean any = nums.stream().anyMatch(n -> n > 2);
      boolean all = nums.stream().allMatch(n -> n < 5);
      setResultFieldEntry("any>2: " + any + ", all<5: " + all);
    };
  }

  public Consumer<Void> testMin() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(10, 5, 20, 3));
      int min = nums.stream().min((a, b) -> a.compareTo(b)).orElse(-1);
      setResultFieldEntry("min(): " + min);
    };
  }

  public Consumer<Void> testMax() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(10, 5, 20, 3));
      int max = nums.stream().max((a, b) -> a.compareTo(b)).orElse(-1);
      setResultFieldEntry("max(): " + max);
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
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 2, 3, 3));
      Set<Integer> uniqueSet = nums.stream().collect(Collectors.toSet());

      List<String> list = new ArrayList<>();
      uniqueSet.forEach(n -> list.add(String.valueOf(n)));

      String joined = String.join(", ", list);
      setResultFieldEntry("collect(toSet): [" + joined + "]");
    };
  }

  public Consumer<Void> testCollectCounting() {
    return (Void) -> {
      List<Integer> nums = getInputIntegers(Arrays.asList(1, 2, 3, 4, 5));
      Long count = nums.stream().collect(Collectors.counting());
      setResultFieldEntry("collect(counting): " + count);
    };
  }
}
