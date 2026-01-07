package gui.test;

import umlp.guiutil.HTMLUtils;
import umlp.jsweet.extension.annotation.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
//import java.time.Month;
//import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.util.function.Consumer;

@Component
public class LocalDataTime extends LocalDataTimeTOP {

  // --- STANDARD GETTERS (LocalDateTime) ---

  @Override
  public Consumer<Void> currentTime() {
    return (Void) -> HTMLUtils.alert("Current Date and Time (ISO): " + LocalDateTime.now().toString());
  }

  @Override
  public Consumer<Void> testGetHour() {
    return (Void) -> HTMLUtils.alert("Hour (0-23): " + LocalDateTime.now().getHour());
  }

  @Override
  public Consumer<Void> testGetMinute() {
    return (Void) -> HTMLUtils.alert("Minute (0-59): " + LocalDateTime.now().getMinute());
  }

  @Override
  public Consumer<Void> testGetSecond() {
    return (Void) -> HTMLUtils.alert("Second (0-59): " + LocalDateTime.now().getSecond());
  }

  @Override
  public Consumer<Void> testGetYear() {
    return (Void) -> HTMLUtils.alert("Year: " + LocalDateTime.now().getYear());
  }

  @Override
  public Consumer<Void> testGetDayOfMonth() {
    return (Void) -> HTMLUtils.alert("Day of Month (1-31): " + LocalDateTime.now().getDayOfMonth());
  }

  @Override
  public Consumer<Void> testGetDayOfYear() {
    return (Void) -> HTMLUtils.alert("Day of Year (1-365/366): " + LocalDateTime.now().getDayOfYear());
  }

  // --- ENUM TESTS (LocalDateTime) ---

  @Override
  public Consumer<Void> testGetMonthValue() {
    // Returns int 1-12
    return (Void) -> HTMLUtils.alert("Month Value (1-12): " + LocalDateTime.now().getMonthValue());
  }

  @Override
  public Consumer<Void> testGetMonth() {
    return (Void) -> HTMLUtils.alert("Month Enum: " + LocalDateTime.now().getMonth());
  }


  @Override
  public Consumer<Void> testGetDayOfWeek() {
    return (Void) -> HTMLUtils.alert("Day of Week Enum: " + LocalDateTime.now().getDayOfWeek());
  }

  // --- PRECISION TESTS ---

  @Override
  public Consumer<Void> testGetNano() {
    return (Void) -> {
      // NOTE: In JS adapter, precision is limited to Milliseconds (digits 1-3).
      // Digits 4-9 will likely be 0.
      int nano = LocalDateTime.now().getNano();
      HTMLUtils.alert("Nano (JS limits this to Millis precision): " + nano);
    };
  }

  @Override
  public Consumer<Void> testGetMillisFromNano() {
    return (Void) -> {
      int nanos = LocalDateTime.now().getNano();
      int millis = nanos / 1000000;
      HTMLUtils.alert("Millis (calculated from Nano): " + millis);
    };
  }

  // --- FACTORY & PARSING (LocalDateTime) ---

  @Override
  public Consumer<Void> testOf() {
    return (Void) -> {
      // Test static factory
      LocalDateTime specificDate = LocalDateTime.of(2025, 12, 24, 18, 30, 0);
      HTMLUtils.alert("Static 'of' (2025-12-24 18:30:00): " + specificDate.toString());
    };
  }

  // --- MANIPULATION (LocalDateTime) ---

  @Override
  public Consumer<Void> testPlus() {
    return (Void) -> {
      LocalDateTime now = LocalDateTime.now();
      // Updated to test 'plusWeeks' specifically as it has special handling in adapter
      LocalDateTime future = now.plusWeeks(1).plusHours(2);
      HTMLUtils.alert("Now + 1 Week + 2 Hours: " + future.toString());
    };
  }

  @Override
  public Consumer<Void> testMinus() {
    return (Void) -> {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime past = now.minusYears(1).minusMinutes(30);
      HTMLUtils.alert("Now - 1 Year - 30 Minutes: " + past.toString());
    };
  }

  // --- COMPARISON (LocalDateTime) ---

  @Override
  public Consumer<Void> testIsAfter() {
    return (Void) -> {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime past = now.minusDays(1);
      HTMLUtils.alert("Now (" + now + ") isAfter yesterday (" + past + ")? " + now.isAfter(past));
    };
  }

  @Override
  public Consumer<Void> testIsBefore() {
    return (Void) -> {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime future = now.plusDays(1);
      HTMLUtils.alert("Now (" + now + ") isBefore tomorrow (" + future + ")? " + now.isBefore(future));
    };
  }

  @Override
  public Consumer<Void> testIsEqual() {
    return (Void) -> {
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime same = LocalDateTime.parse(now.toString());

      // Detailed Output: Shows both values + the result
      HTMLUtils.alert(
          "Original: " + now.toString() + "\n" +
              "Parsed:   " + same.toString() + "\n" +
              "isEqual?  " + now.isEqual(same)
      );
    };
  }

  public Consumer<Void> testCompareTo() {
    return (Void) -> {
      LocalDateTime a = LocalDateTime.of(2023, 1, 1, 10, 0);
      LocalDateTime b = LocalDateTime.of(2023, 1, 1, 12, 0);
      // CompareTo should be negative if a < b
      int result = a.compareTo(b);
      HTMLUtils.alert("CompareTo (10:00 vs 12:00): " + result + " (Expected negative)");
    };
  }

  // --- EPOCH / TIMESTAMP ---

  @Override
  public Consumer<Void> testToEpochSecond() {
    return (Void) -> {
      LocalDateTime now = LocalDateTime.now();
      // Adapter simulates UTC, so passing ZoneOffset.UTC aligns with adapter logic
      long epoch = now.toEpochSecond(ZoneOffset.UTC);
      HTMLUtils.alert("Current Epoch Second: " + epoch);
    };
  }

  @Override
  public Consumer<Void> testOfEpochSecond() {
    return (Void) -> {
      long timestamp = 1672531200L; // 2023-01-01 00:00:00 UTC
      LocalDateTime date = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
      HTMLUtils.alert("From Epoch 1672531200: " + date.toString());
    };
  }

  // ==========================================================================================
  // NEW: LOCALDATE TESTS
  // ==========================================================================================

  public Consumer<Void> testLocalDateNow() {
    return (Void) -> {
      LocalDate today = LocalDate.now();
      HTMLUtils.alert("LocalDate Now: " + today.toString());
    };
  }

  public Consumer<Void> testLocalDateOf() {
    return (Void) -> {
      LocalDate specificDate = LocalDate.of(2025, 5, 20);
      HTMLUtils.alert("LocalDate Of (2025-05-20): " + specificDate.toString());
    };
  }

  public Consumer<Void> testLocalDateManipulation() {
    return (Void) -> {
      LocalDate start = LocalDate.now();
      LocalDate result = start.plusWeeks(2).minusDays(1).plusYears(1);

      HTMLUtils.alert(
          "Start: " + start.toString() + "\n" +
              "Result (+2 Weeks, -1 Day, +1 Year): " + result.toString()
      );
    };
  }

  public Consumer<Void> testLocalDateComparison() {
    return (Void) -> {
      LocalDate d1 = LocalDate.of(2023, 1, 1);
      LocalDate d2 = LocalDate.of(2023, 12, 31);

      boolean isBefore = d1.isBefore(d2);
      boolean isAfter = d1.isAfter(d2);

      HTMLUtils.alert("2023-01-01 isBefore 2023-12-31? " + isBefore + "\n" +
          "2023-01-01 isAfter 2023-12-31? " + isAfter);
    };
  }

  public Consumer<Void> testLocalDateParse() {
    return (Void) -> {
      LocalDate parsed = LocalDate.parse("2024-02-29");
      HTMLUtils.alert("Parsed Leap Year Date: " + parsed.toString());
    };
  }
}
