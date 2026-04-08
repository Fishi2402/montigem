package gui.test;

import def.dom.HTMLInputElement;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

import static def.dom.Globals.document;

@Component
public class OptionalAdapterTest extends OptionalAdapterTestTOP {

  private void setResultFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("resultIdInput");
    if (el != null) {
      el.value = value;
    }
  }

  private String getRawInput() {
    HTMLInputElement el = (HTMLInputElement) document.getElementById("inputFieldIdInput");
    if (el != null && el.value != null && !el.value.trim().isEmpty()) {
      return el.value.trim();
    }
    return null;
  }

  @Override
  public Consumer<Void> testOf() {
    return (Void) -> {
      String input = getRawInput();
      if (input == null) input = "default";
      Optional<String> opt = Optional.of(input);
      setResultFieldEntry("of: " + opt.get());
    };
  }

  @Override
  public Consumer<Void> testOfNullable() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      setResultFieldEntry("ofNullable: " + (opt.isPresent() ? opt.get() : "empty"));
    };
  }

  @Override
  public Consumer<Void> testEmpty() {
    return (Void) -> {
      Optional<String> opt = Optional.empty();
      setResultFieldEntry("empty: " + (opt.isPresent() ? "present" : "empty"));
    };
  }

  @Override
  public Consumer<Void> testIsPresent() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      setResultFieldEntry("isPresent: " + opt.isPresent());
    };
  }

  @Override
  public Consumer<Void> testIsEmpty() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      setResultFieldEntry("isEmpty: " + opt.isEmpty());
    };
  }

  @Override
  public Consumer<Void> testGet() {
    return (Void) -> {
      String input = getRawInput();
      if (input == null) input = "fallback";
      Optional<String> opt = Optional.of(input);
      setResultFieldEntry("get: " + opt.get());
    };
  }

  @Override
  public Consumer<Void> testOrElse() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      setResultFieldEntry("orElse: " + opt.orElse("other"));
    };
  }

  @Override
  public Consumer<Void> testOrElseGet() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      setResultFieldEntry("orElseGet: " + opt.orElseGet(() -> "otherGet"));
    };
  }

  @Override
  public Consumer<Void> testMap() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      Optional<Integer> mapped = opt.map(s -> s.length());
      setResultFieldEntry("map: " + (mapped.isPresent() ? String.valueOf(mapped.get()) : "empty"));
    };
  }

  @Override
  public Consumer<Void> testFlatMap() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      Optional<Integer> flatMapped = opt.flatMap(s -> Optional.of(s.length()));
      setResultFieldEntry("flatMap: " + (flatMapped.isPresent() ? String.valueOf(flatMapped.get()) : "empty"));
    };
  }

  @Override
  public Consumer<Void> testFilter() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      Optional<String> filtered = opt.filter(s -> s.length() > 3);
      setResultFieldEntry("filter: " + (filtered.isPresent() ? filtered.get() : "empty"));
    };
  }

  @Override
  public Consumer<Void> testIfPresent() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      StringBuilder sb = new StringBuilder();
      opt.ifPresent(s -> sb.append("present: ").append(s));
      if (sb.length() == 0) sb.append("not present");
      setResultFieldEntry("ifPresent: " + sb.toString());
    };
  }

  @Override
  public Consumer<Void> testIfPresentOrElse() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      StringBuilder sb = new StringBuilder();
      opt.ifPresentOrElse(s -> sb.append("present: ").append(s), () -> sb.append("else empty"));
      setResultFieldEntry("ifPresentOrElse: " + sb.toString());
    };
  }

  @Override
  public Consumer<Void> testOr() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      Optional<String> result = opt.or(() -> Optional.of("or fallback"));
      setResultFieldEntry("or: " + result.get());
    };
  }

  @Override
  public Consumer<Void> testStream() {
    return (Void) -> {
      String input = getRawInput();
      Optional<String> opt = Optional.ofNullable(input);
      long count = opt.stream().count();
      setResultFieldEntry("stream: " + count);
    };
  }

  @Override
  public Consumer<Void> testOfNull() {
    return (Void) -> {
      try {
        Optional.of(null);
        setResultFieldEntry("ofNull: no error");
      } catch (Exception e) {
        setResultFieldEntry("ofNull: error " + e.getMessage());
      }
    };
  }
}
