import { TypeCheckerUtil } from "./type-checker.util";

/**
 * Utility class that provides static helper methods for cleaning objects (= removing "nullish" values (null, undefined,
 * empty arrays, empty maps));
 */
export class CleanUtil {
  /**
   * Recursively cleans an object, array, Set, or Map by removing nullish values.
   *
   * @param {any} data - The data to clean.
   * @returns {any} The cleaned data.
   */
  public static cleanDeep(data: any): any {
    if (Array.isArray(data)) return this.cleanArray(data);
    if (data instanceof Map) return this.cleanMap(data);
    if (data instanceof Set) return this.cleanSet(data);
    if (data instanceof Date || typeof data !== "object" || data === null) {
      return data;
    }
    if (data && typeof data === "object") return this.cleanObject(data);
  }

  /**
   * Cleans an array by removing nullish values.
   *
   * @param {any[]} data - The array to clean.
   * @returns {any[]} The cleaned array.
   */
  public static cleanArray(data: any[]): any[] {
    return data.reduce((accumulator: any[], value) => {
      const cleaned = this.cleanDeep(value);
      if (!TypeCheckerUtil.isNullish(cleaned)) {
        accumulator.push(cleaned);
      }
      return accumulator;
    }, []);
  }

  /**
   * Cleans a Map by removing nullish values.
   *
   * @param {Map<any, any>} data - The Map to clean.
   * @returns {Map<any, any>} The cleaned Map.
   */
  public static cleanMap(data: Map<any, any>): Map<any, any> {
    const cleanMap = new Map();
    data.forEach((value, key) => {
      const cleaned = this.cleanDeep(value);
      if (!TypeCheckerUtil.isNullish(cleaned)) {
        cleanMap.set(key, cleaned);
      }
    });
    return cleanMap;
  }

  /**
   * Cleans a Set by removing nullish values.
   *
   * @param {Set<any>} data - The Set to clean.
   * @returns {Set<any>} The cleaned Set.
   */
  public static cleanSet(data: Set<any>): Set<any> {
    const cleanSet = new Set();
    data.forEach((value) => {
      const cleaned = this.cleanDeep(value);
      if (!TypeCheckerUtil.isNullish(cleaned)) {
        cleanSet.add(cleaned);
      }
    });
    return cleanSet;
  }

  /**
   * Cleans an object by removing nullish values.
   *
   * @param {Record<string, any>} data - The object to clean.
   * @returns {Record<string, any>} The cleaned object.
   */
  public static cleanObject(data: Record<string, any>): Record<string, any> {
    return Object.keys(data).reduce((accumulator, key) => {
      const cleaned = this.cleanDeep(data[key]);
      if (!TypeCheckerUtil.isNullish(cleaned)) {
        accumulator[key] = cleaned;
      }
      return accumulator;
    }, {} as any);
  }
}
