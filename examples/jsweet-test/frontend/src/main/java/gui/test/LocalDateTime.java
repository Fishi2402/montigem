package gui.test;

import def.dom.HTMLInputElement;
import umlp.jsweet.extension.annotation.Component;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.function.Consumer;

import static def.dom.Globals.document;

@Component
public class LocalDateTime extends LocalDateTimeTOP {

  private void setResultFieldEntry(String value) {
    HTMLInputElement el = (HTMLInputElement) document.getElementById(getResultFieldId() + "Input");
    if (el != null) {
      el.value = value;
    } else {
      System.err.println("Could not find input element with ID: " + getResultFieldId() + "Input");
    }
  }

  private java.time.LocalDateTime getInputOrNow() {
    // ID of the input field from the .gui file (inputTestFieldId) + "Input"
    HTMLInputElement el = (HTMLInputElement) document.getElementById("inputTestFieldIdInput");

    if (el != null && el.value != null && !el.value.isEmpty()) {
      try {
        // Expects ISO format, e.g., "2026-01-01T15:30:45"
        return java.time.LocalDateTime.parse(el.value);
      } catch (Exception e) {
        System.err.println("Parse error using Input-Date (fallback to NOW): " + e.getMessage());
      }
    }
    return java.time.LocalDateTime.now();
  }

  // --- STANDARD GETTERS (Now utilize Input-Date) ---

  @Override
  public Consumer<Void> currentTime() {
    return (Void) -> setResultFieldEntry("Current Date and Time (ISO): " + getInputOrNow().toString());
  }

  @Override
  public Consumer<Void> testGetHour() {
    return (Void) -> setResultFieldEntry("Hour (0-23): " + getInputOrNow().getHour());
  }

  @Override
  public Consumer<Void> testGetMinute() {
    return (Void) -> setResultFieldEntry("Minute (0-59): " + getInputOrNow().getMinute());
  }

  @Override
  public Consumer<Void> testGetSecond() {
    return (Void) -> setResultFieldEntry("Second (0-59): " + getInputOrNow().getSecond());
  }

  @Override
  public Consumer<Void> testGetYear() {
    return (Void) -> setResultFieldEntry("Year: " + getInputOrNow().getYear());
  }

  @Override
  public Consumer<Void> testGetDayOfMonth() {
    return (Void) -> setResultFieldEntry("Day of Month (1-31): " + getInputOrNow().getDayOfMonth());
  }

  @Override
  public Consumer<Void> testGetDayOfYear() {
    return (Void) -> setResultFieldEntry("Day of Year (1-365/366): " + getInputOrNow().getDayOfYear());
  }

  // --- ENUM TESTS (Now utilize Input-Date) ---

  @Override
  public Consumer<Void> testGetMonthValue() {
    return (Void) -> setResultFieldEntry("Month Value (1-12): " + getInputOrNow().getMonthValue());
  }

  @Override
  public Consumer<Void> testGetMonth() {
    return (Void) -> setResultFieldEntry("Month Enum: " + getInputOrNow().getMonth());
  }

  @Override
  public Consumer<Void> testGetDayOfWeek() {
    return (Void) -> setResultFieldEntry("Day of Week Enum: " + getInputOrNow().getDayOfWeek());
  }

  // --- PRECISION TESTS (Now utilize Input-Date) ---

  @Override
  public Consumer<Void> testGetNano() {
    return (Void) -> {
      int nano = getInputOrNow().getNano();
      setResultFieldEntry("Nano (JS limits this to Millis precision): " + nano);
    };
  }

  @Override
  public Consumer<Void> testGetMillisFromNano() {
    return (Void) -> {
      int nanos = getInputOrNow().getNano();
      int millis = nanos / 1000000;
      setResultFieldEntry("Millis (calculated from Nano): " + millis);
    };
  }

  // --- FACTORY & MANIPULATION ---

  @Override
  public Consumer<Void> testOf() {
    return (Void) -> {
      java.time.LocalDateTime specificDate = java.time.LocalDateTime.of(2025, 12, 24, 18, 30, 0);
      setResultFieldEntry("Static 'of': " + specificDate.toString());
    };
  }

  @Override
  public Consumer<Void> testPlus() {
    return (Void) -> {
      java.time.LocalDateTime base = getInputOrNow();
      java.time.LocalDateTime future = base.plusWeeks(1).plusHours(2);
      setResultFieldEntry("Now + 1 Week + 2 Hours: " + future.toString());
    };
  }

  @Override
  public Consumer<Void> testMinus() {
    return (Void) -> {
      java.time.LocalDateTime base = getInputOrNow();
      java.time.LocalDateTime past = base.minusYears(1).minusMinutes(30);
      setResultFieldEntry("Now - 1 Year - 30 Minutes: " + past.toString());
    };
  }

  // --- COMPARISON ---

  @Override
  public Consumer<Void> testIsAfter() {
    return (Void) -> {
      java.time.LocalDateTime base = getInputOrNow();
      java.time.LocalDateTime past = base.minusDays(1);
      setResultFieldEntry("isAfter yesterday? " + base.isAfter(past));
    };
  }

  @Override
  public Consumer<Void> testIsBefore() {
    return (Void) -> {
      java.time.LocalDateTime base = getInputOrNow();
      java.time.LocalDateTime future = base.plusDays(1);
      setResultFieldEntry("isBefore tomorrow? " + base.isBefore(future));
    };
  }

  @Override
  public Consumer<Void> testIsEqual() {
    return (Void) -> {
      java.time.LocalDateTime base = getInputOrNow();
      java.time.LocalDateTime same = java.time.LocalDateTime.parse(base.toString());
      setResultFieldEntry("isEqual? " + base.isEqual(same));
    };
  }

  // --- EPOCH ---

  @Override
  public Consumer<Void> testToEpochSecond() {
    return (Void) -> {
      long epoch = getInputOrNow().toEpochSecond(ZoneOffset.UTC);
      setResultFieldEntry("Current Epoch Second: " + epoch);
    };
  }

  @Override
  public Consumer<Void> testOfEpochSecond() {
    // Tests static factory (remains fixed, as a specific timestamp is tested)
    return (Void) -> {
      long timestamp = 1672531200L; // 2023-01-01 00:00:00 UTC
      java.time.LocalDateTime date = java.time.LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
      setResultFieldEntry("From Epoch 1672531200: " + date.toString());
    };
  }

  // --- LOCALDATE ---

  public Consumer<Void> testLocalDateNow() {
    return (Void) -> setResultFieldEntry("LocalDate Now: " + LocalDate.now().toString());
  }

  public Consumer<Void> testLocalDateOf() {
    return (Void) -> setResultFieldEntry("LocalDate Of: " + LocalDate.of(2025, 5, 20).toString());
  }

  public Consumer<Void> testLocalDateManipulation() {
    // Here we use LocalDate.now() as base, similar to original, as we primarily test LocalDateTime inputs above.
    return (Void) -> {
      LocalDate start = LocalDate.now();
      LocalDate result = start.plusWeeks(2).minusDays(1).plusYears(1);
      setResultFieldEntry("Result (+2 Weeks, -1 Day, +1 Year): " + result.toString());
    };
  }
}
