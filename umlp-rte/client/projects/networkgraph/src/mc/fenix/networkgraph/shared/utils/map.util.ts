import { ComparisonFunction, SortingMode } from "./types";
import { TypeCheckerUtil } from "./type-checker.util";
import { compareValues } from "./comparison.util";
import { ArrayUtil } from "./array.util";
import { CleanUtil } from "./clean.util";

/**
 * The `MapUtils` class provides utility functions for working with Map objects. It provides the following static
 * methods:
 *
 * @example
 *   // Removes null, undefined, and empty array keys/values from a map.
 *   CleanMap();
 *   // Returns Map keys sorted by a specified property value.
 *   GetMapKeysSortedByProperty();
 *   // Sorts a Map by its keys in ascending or descending order.
 *   SortMapByKey();
 *   // Converts a Map into an Object.
 *   ConvertMapToObject();
 */
export class MapUtil {
  /**
   * Takes a Map and a property name as input, and returns an array of the Map's keys sorted in ascending order based on
   * the values of the specified property of the Map's value objects.
   *
   * @remarks
   *   - This function expects that the Map's values are objects and the specified property exists in these objects.
   *
   * @template K - The type of the keys in the input Map
   * @template V - The type of the values in the input Map, expected to be an object type
   * @template P - The type of the property in V by which the keys will be sorted, expected to be a
   * @param {Map<K, V>} map - The Map to sort, which should have objects as values
   * @param {P} propertyName - The name of the property in V (objects of the map) to use for sorting the keys
   * @param {SortingMode} mode - The sorting mode. Default is 'ASC' (ascending). Can be set to 'DESC' for descending
   *   order.
   * @param {ComparisonFunction<V[P] | undefined>} compareFunction - An optional custom compare function.
   * @returns {K[]} An array of the Map's keys sorted in ascending order based on the values of the propertyName in the
   *   Map's value objects
   */
  public static getMapKeysSortedByProperty<K, V extends Record<P, unknown>, P extends keyof V>(
    map: Map<K, V>,
    propertyName: P,
    mode: SortingMode = SortingMode.ASC,
    compareFunction?: ComparisonFunction<V[P]>
  ): Array<K> {
    // Remove empty and nullish entries
    const cleanedMap: Map<K, V> = CleanUtil.cleanDeep(map);

    // Check if every entry has the specified property
    if (!ArrayUtil.hasObjectsWithProperty([...map.values()], propertyName)) {
      throw new Error(`Map's values are not objects or do not contain property '${String(propertyName)}'`);
    }

    return [...cleanedMap.entries()]
      .sort(([, aValue]: [K, V], [, bValue]: [K, V]) =>
        compareValues(aValue[propertyName], bValue[propertyName], mode, compareFunction)
      )
      .map(([key]: [K, V]) => key);
  }

  /**
   * Sorts a Map object by its keys in ascending or descending order.
   *
   * @remarks
   *   - "Nullish" (null, undefined, or empty) entries are removed before sorting.
   *
   * @template K - Type of the map keys.
   * @template V - Type of the map values.
   * @param {Map<K, V>} map - The Map object to be sorted.
   * @param {SortingMode} mode - The sorting mode. Default is 'ASC' (ascending). Can be set to 'DESC' for descending
   *   order.
   * @param {ComparisonFunction<K>} compareFunction - An optional custom compare function.
   * @returns {Map<K, V>} A new sorted Map object.
   */
  public static sortMapByKey<K, V>(
    map: Map<K, V>,
    mode: SortingMode = SortingMode.ASC,
    compareFunction?: ComparisonFunction<K>
  ): Map<K, V> {
    // Remove empty and nullish entries
    const cleanedMap: Map<K, V> = CleanUtil.cleanDeep(map);
    if (TypeCheckerUtil.isNullish(cleanedMap)) return new Map();

    return new Map(
      [...cleanedMap.entries()].sort(([aKey]: [K, V], [bKey]: [K, V]): number =>
        compareValues(aKey, bKey, mode, compareFunction)
      )
    );
  }

  /**
   * Convert a Map into an Object.
   *
   * @template K - Type of the map keys (must extend PropertyKey so that they can be converted to an object)
   * @template V - Type of the map values
   * @param {Map<K, V>} map - The Map to be converted into an Object.
   * @returns {Record<K, V>} The converted object from Map.
   */
  public static convertMapToObject<K extends PropertyKey, V>(map: Map<K, V>): Record<K, V> {
    // Convert Map entries to an array, reduce them into an object
    return Object.fromEntries(map) as Record<K, V>;
  }

  /**
   * Checks if the given map has only keys of type PropertyKey. (string, number, or a symbol).
   *
   * @param {unknown} map - The map to be checked. It can be of any type.
   * @returns {boolean} - A type guard that asserts map is a Map<PropertyKey, unknown> if the map has only keys of type
   *   PropertyKey. Otherwise, it returns false.
   */
  public static hasPropertyKeysOnly(map: unknown): map is Map<PropertyKey, unknown> {
    return TypeCheckerUtil.isMap(map) && [...map.keys()].every((element) => TypeCheckerUtil.isPropertyKey(element));
  }
}
