import { Pipe, PipeTransform } from "@angular/core";

/**
 * NodeKeyFormatterPipe is a pipe that formats a node key for display. If the key is a number, it prefixes it with '#'.
 * Otherwise, it wraps the key in parentheses.
 *
 * @example
 *   <!-- Usage in HTML -->
 *   {{ '123' | nodeKeyFormatter }}  <!-- Output: (#123) -->
 *   {{ 'ABC' | nodeKeyFormatter }}  <!-- Output: (ABC) -->
 */
@Pipe({
  name: "nodeKeyFormatter",
  standalone: true,
})
export class NodeKeyFormatterPipe implements PipeTransform {
  /**
   * Transforms a node key into a formatted string for display.
   *
   * @param {string} key - The node key to be formatted.
   * @returns {string} - The formatted node key.
   */
  transform(key: string): string {
    const parsedKey: number = Number(key);
    return Number.isNaN(parsedKey) ? `(${key})` : `(#${parsedKey})`;
  }
}
