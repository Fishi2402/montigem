// format-value.pipe.ts
import { Pipe, PipeTransform } from "@angular/core";
import { TypeCheckerUtil } from "../utils/type-checker.util";
import { DateUtil } from "../utils/date.util";

/**
 * FormatValuePipe is a pipe that transforms raw values into formatted strings for display. It handles different types
 * of values including null/undefined, numbers, dates, and booleans.
 *
 * @example
 *   {{ value | formatValue }}
 *
 * @see TypeCheckerUtil
 * @see DateUtil
 */
@Pipe({
  name: "formatValue",
  standalone: true,
})
export class FormatValuePipe implements PipeTransform {
  /**
   * Transforms a raw value into a formatted string based on its type.
   *
   * - Null or undefined: 'N/A'.
   * - Number values: Commas separating thousands and decimals up to 2 places.
   * - Date values: Localized date strings.
   * - Boolean values: 'Yes' or 'No'.
   * - Other values: Using their toString method.
   *
   * @param {unknown} value - The value to be formatted.
   * @param {Intl.LocalesArgument} [selectedLocale] - The locale to use for formatting. Default is `'en-US'`
   * @param {Intl.NumberFormatOptions} numberOptions - Options for formatting numbers. Default is `{
   *   maximumFractionDigits: 2 }`
   * @returns {string} - The formatted value as a string.
   */
  transform(
    value: unknown,
    selectedLocale: Intl.LocalesArgument = "en-US",
    numberOptions: Intl.NumberFormatOptions = { maximumFractionDigits: 2 }
  ): string {
    if (TypeCheckerUtil.isArray(value)) {
      return "[" + value.map((v) => this.transform(v, selectedLocale, numberOptions)).join(", ") + "]";
    }
    if (TypeCheckerUtil.isNullOrUndefined(value)) return "N/A";
    if (TypeCheckerUtil.isNumber(value)) {
      return value.toLocaleString(selectedLocale, numberOptions);
    }
    if (TypeCheckerUtil.isDate(value)) {
      return DateUtil.parseDate(value)?.toLocaleDateString(selectedLocale) ?? "N/A";
    }
    if (TypeCheckerUtil.isBoolean(value)) {
      return value ? "Yes" : "No";
    }
    return String(value);
  }
}
