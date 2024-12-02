import { TypeCheckerUtil } from "./type-checker.util";

const isEqual = require("lodash.isequal");
const cloneDeepWith = require("lodash.clonedeepwith");
/**
 * Utility class with static helper methods.
 *
 * @example
 *   // Deep copy of an object (without circular references!)
 *   deepCopy(object);
 *   // Deeply check whether two objects are equal in all their properties;
 *   isequal(object1, object2);
 */
export class Utils {
  /**
   * Creates a deep copy of the given object.
   *
   * @param {any} object - The object to copy.
   * @returns {any} The copied object.
   */
  public static deepCopy<T>(object: T): T {
    return cloneDeepWith(object, (value: any) => {
      // If the value is a function, return it as-is (don't clone)
      return TypeCheckerUtil.isFunction(value) ? value : undefined;
    });
  }

  /**
   * Checks if two objects are equal by value.
   *
   * @param {any} object1 - The first object.
   * @param {any} object2 - The second object.
   * @returns {boolean} True if the objects are equal by value, false otherwise.
   */
  public static isEqual(object1: any, object2: any): boolean {
    return isEqual(object1, object2);
  }
}
