import { TypeCheckerUtil } from "./type-checker.util";

export enum DateFormat {
  YEAR = "yyyy",
  MONTH = "MM",
  DAY = "dd",
  HOUR = "HH",
  MINUTE = "mm",
  TIMEZONE = "ZZZZ",
}

export class DateUtil {
  /** Hour pattern that matches any two-digit number from 00 to 23. */
  private static readonly HOUR_PATTERN: string = "(?:[01]\\d|2[0-3])";

  /** Minute pattern that matches any two-digit number from 00 to 59. */
  private static readonly MINUTE_PATTERN: string = "[0-5]\\d";

  /** Timezone pattern that matches four digits for timezone offset. */
  private static readonly TIMEZONE_PATTERN: string = "(?:\\+|\\-)\\d{4}";

  /**
   * Day pattern that matches numbers from 1 to 31, allowing for optional leading zeros. The pattern prioritizes
   * matching higher numbers (31, 30, [12] followed by any digit) before matching single-digit numbers with optional
   * leading zero.
   */
  private static readonly DAY_PATTERN: string = "(31|30|[12]\\d|0?[1-9])";

  /**
   * Month pattern that matches numbers from 1 to 12, allowing for optional leading zeros. Similar to the day pattern,
   * it prioritizes matching higher numbers (1[0-2]) before matching single-digit numbers with optional leading zero.
   */
  private static readonly MONTH_PATTERN: string = "(1[0-2]|0?[1-9])";

  /** Year pattern that matches any four-digit number from 1900 to 2099. */
  private static readonly YEAR_PATTERN: string = "((?:19|20)\\d{2})";

  /** Start-of-line expression ensures that the pattern matches the entire line from the beginning. */
  private static readonly START_EXPRESSION: string = "^";

  /** End-of-line expression ensures that the pattern matches the entire line to the end. */
  private static readonly END_EXPRESSION: string = "$";

  /**
   * Separator pattern that matches common date separators: `.` `/` `-` or `space` (i.e.,`22-08-2023`,`22/08/2023`, or
   * `22.08.2023`.). The `?` makes the separator optional, allowing for date formats with or without separators.
   */
  private static readonly SEPARATOR_PATTERN: string = "[./\\- ]?";

  /**
   * Array of supported date formats and corresponding patterns. Each object defines a format as an array of DateFormat
   * and a pattern as a RegExp constructed using the above patterns.
   */
  private static readonly DATE_FORMATS: { format: DateFormat[]; pattern: RegExp }[] = [
    {
      format: [DateFormat.YEAR, DateFormat.MONTH, DateFormat.DAY],
      pattern: new RegExp(
        this.START_EXPRESSION +
          [DateUtil.YEAR_PATTERN, DateUtil.MONTH_PATTERN, DateUtil.DAY_PATTERN].join(DateUtil.SEPARATOR_PATTERN) +
          this.END_EXPRESSION
      ),
    },
    {
      format: [DateFormat.DAY, DateFormat.MONTH, DateFormat.YEAR],
      pattern: new RegExp(
        this.START_EXPRESSION +
          [DateUtil.DAY_PATTERN, DateUtil.MONTH_PATTERN, DateUtil.YEAR_PATTERN].join(DateUtil.SEPARATOR_PATTERN) +
          this.END_EXPRESSION
      ),
    },
    {
      format: [DateFormat.MONTH, DateFormat.DAY, DateFormat.YEAR],
      pattern: new RegExp(
        this.START_EXPRESSION +
          [DateUtil.MONTH_PATTERN, DateUtil.DAY_PATTERN, DateUtil.YEAR_PATTERN].join(DateUtil.SEPARATOR_PATTERN) +
          this.END_EXPRESSION
      ),
    },
    {
      format: [
        DateFormat.DAY,
        DateFormat.MONTH,
        DateFormat.YEAR,
        DateFormat.HOUR,
        DateFormat.MINUTE,
        DateFormat.TIMEZONE,
      ],
      pattern: new RegExp(
        `${DateUtil.DAY_PATTERN}\\.${DateUtil.MONTH_PATTERN}\\.${DateUtil.YEAR_PATTERN}-${DateUtil.HOUR_PATTERN}:${DateUtil.MINUTE_PATTERN}${DateUtil.TIMEZONE_PATTERN}`
      ),
    },
  ];

  /**
   * Regex to match a string that is in the common ISO 8601 Date Format.
   *
   * Examples of matching patterns:
   *
   * - "2023-08-25": Date only.
   * - "2023-08-25T13:45:30": Combined date and time.
   * - "2023-08-25T13:45:30Z": Combined date and time with UTC timezone.
   * - "2023-08-25T13:45:30.123Z": Combined date and time with milliseconds and UTC timezone.
   * - "2023-08-25T13:45:30+01:00": Combined date and time with a specific timezone offset.
   */
  private static readonly ISO_REGEX: RegExp =
    /^\d{4}-\d{2}-\d{2}(T\d{2}:\d{2}:\d{2}(?:\.\d+)?(?:[Zz]|[+-]\d{2}:\d{2})?)?$/;

  /**
   * Parses a given value into a Date object using supported date formats. If the value is already a Date object or can
   * be natively parsed into a Date, it is returned. Otherwise, the method attempts to parse the value using defined
   * date patterns.
   *
   * @param {unknown} value - The value to parse into a date.
   * @returns {Date | null} - A Date object if parsing is successful, or null if parsing fails.
   */
  public static parseDate(value: unknown): Date | null {
    // Try to parse it using native date
    const nativeDate: Date | null = this.nativeDateOrISO(value);
    if (nativeDate) return nativeDate;

    // Try to parse it using regex
    return this.parseDateByFormats(String(value));
  }

  /**
   * Checks whether a given value represents a valid date according to supported formats. The method first tries to
   * parse the value using native JavaScript Date utilities, then checks against defined date patterns.
   *
   * @param {unknown} value - The value to check for validity as a date.
   * @returns {boolean} - True if the value represents a valid date, false otherwise.
   */
  public static isDate(value: unknown): boolean {
    if (!TypeCheckerUtil.isString(value) && !(value instanceof Date)) return false;
    // Try to parse it using native date
    const nativeDate: Date | null = this.nativeDateOrISO(value);
    if (nativeDate) return true;

    // Try to parse it using regex
    return this.DATE_FORMATS.some(({ pattern }: { pattern: RegExp }) => pattern.test(String(value)));
  }

  /**
   * Attempts to parse a value into a Date object using native JavaScript Date utilities. Returns the parsed Date object
   * if successful or null if the value cannot be parsed.
   *
   * @param {unknown} value - The value to attempt to parse into a date.
   * @returns {Date | null} - A Date object if parsing is successful, or null if parsing fails.
   */
  private static nativeDateOrISO(value: unknown): Date | null {
    if (value instanceof Date) return isNaN(value.getTime()) ? null : value;

    // Only try to parse strings
    if (!TypeCheckerUtil.isString(value)) return null;

    // Check if the value matches the ISO 8601 pattern
    if (!this.ISO_REGEX.test(value)) return null;

    const parsedDate: Date = new Date(value);
    const year: number = parsedDate.getFullYear();

    // Check if the year falls within the acceptable range
    if (year < 1900 || year > 2099) return null;

    if (!Number.isNaN(parsedDate.getTime())) return parsedDate;

    return null;
  }

  /**
   * Attempts to parse a date string into a Date object using the supported date formats defined in `DATE_FORMATS`.
   * Iterates through the defined formats, attempting to match the value with each pattern. Returns the parsed Date
   * object if successful or null if no match is found.
   *
   * @param {string} value - The date string to parse.
   * @returns {Date | null} - A Date object if parsing is successful, or null if parsing fails.
   */
  private static parseDateByFormats(value: string): Date | null {
    for (const { pattern, format } of this.DATE_FORMATS) {
      const match: RegExpMatchArray | null = value.match(pattern);
      if (match) {
        const dateComponents: { [key: string]: number | string } = {};
        format.forEach((component: DateFormat, index: number): void => {
          dateComponents[component] = match[index + 1];
        });

        const date = new Date(
          Number(dateComponents[DateFormat.YEAR]),
          Number(dateComponents[DateFormat.MONTH]) - 1,
          Number(dateComponents[DateFormat.DAY]),
          Number(dateComponents[DateFormat.HOUR] ?? 0),
          Number(dateComponents[DateFormat.MINUTE] ?? 0)
        );

        // Handle timezone if it's in the format
        if (dateComponents[DateFormat.TIMEZONE]) {
          const timezoneOffset = Number(dateComponents[DateFormat.TIMEZONE]);
          const offsetMinutes = Math.floor(timezoneOffset / 100) * 60 + (timezoneOffset % 100);
          date.setMinutes(date.getMinutes() - date.getTimezoneOffset() - offsetMinutes);
        }

        return date;
      }
    }

    return null;
  }
}
