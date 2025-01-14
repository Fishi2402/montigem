import { Pipe, PipeTransform } from "@angular/core";

/**
 * A pipe for transforming an object into an array of its keys.
 *
 * Usage: object | objectKeys Example: { a: 1, b: 2 } | objectKeys // returns ['a', 'b']
 */
@Pipe({
  name: "objectKeys",
  standalone: true,
})
export class ObjectKeysPipePipe implements PipeTransform {
  /**
   * Transforms an object into an array of its keys.
   *
   * @param {any} value - The object whose keys are to be returned.
   * @returns {string[]} An array of the object's keys, or an empty array if the input is null, undefined, or not an
   *   object.
   */
  transform(value: unknown): string[] {
    return !value || typeof value !== "object" ? [] : Object.keys(value);
  }
}
