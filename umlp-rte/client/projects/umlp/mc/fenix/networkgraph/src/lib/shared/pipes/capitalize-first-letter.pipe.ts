import { Pipe, PipeTransform } from "@angular/core";

/** Capitalize the first letter of each word in a string. */
@Pipe({
  name: "capitalizeFirstLetter",
  standalone: true,
})
export class CapitalizeFirstLetterPipe implements PipeTransform {
  /**
   * @param {string} value - The string to transform
   * @returns {string} Transformed string with each word's first letter capitalized
   */
  transform(value: string): string {
    if (!value) return value;
    return value
      .split(" ")
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
      .join(" ");
  }
}
