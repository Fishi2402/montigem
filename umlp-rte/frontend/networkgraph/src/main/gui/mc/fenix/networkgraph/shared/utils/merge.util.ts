import { TypeCheckerUtil, TypeEnum } from "./type-checker.util";
import { DeepMergeOptions } from "./types";

import { Utils } from "./utils";

/** Utility class with static helper methods. */
export class MergeUtil {
  /**
   * Default options to be used in deep merging.
   *
   * @remarks
   *   This constant sets the default behavior for deep merging operations.
   */
  private static readonly DEFAULT_MERGE_OPTIONS: DeepMergeOptions = {
    /** Boolean flag indicating whether to deep-merge objects, or only return the "new" one. */
    deepMergeObjects: true,

    /** Boolean flag indicating whether to deep-merge maps, or only return the "new" one. */
    deepMergeMaps: true,

    /** Boolean flag indicating whether to deep-merge arrays, or only return the "new" one. */
    deepMergeArrays: true,

    /** Boolean flag indicating whether to deep-merge sets, or only return the "new" one. */
    deepMergeSets: true,
  };

  /**
   * Merges multiple objects together, handling arrays, Maps, and Sets appropriately.
   *
   * @param {...any[]} objects - The objects to merge.
   * @returns {any} The merged object.
   */
  public static deepMerge(...objects: any[]): any {
    return this.deepMergeWithOptions(this.DEFAULT_MERGE_OPTIONS, ...objects);
  }

  /**
   * Merges multiple objects together, handling arrays, Maps, and Sets appropriately, and based on provided !options.
   *
   * @remarks
   *   This function allows specifying the merging behavior (whether to deep-merge or replace) for each type of object.
   *   The options can be used to control how arrays, maps, and sets are merged.
   * @param {DeepMergeOptions} options - The options to use for merging.
   * @param {...any[]} objects - The objects to merge.
   * @returns {any} The merged object.
   */
  public static deepMergeWithOptions(options: Partial<DeepMergeOptions>, ...objects: any[]): any {
    if (objects.length <= 1) return objects[0] || {};

    let target = objects[0];
    const targetType: TypeEnum = TypeCheckerUtil.getType(target);

    for (const source of objects.slice(1)) {
      const sourceType: TypeEnum = TypeCheckerUtil.getType(source);
      if (TypeCheckerUtil.areAllExpectedType(TypeEnum.FUNCTION, sourceType, targetType)) {
        target = source;
      } else if (TypeCheckerUtil.areAllExpectedType(TypeEnum.ARRAY, sourceType, targetType)) {
        target = MergeUtil.mergeArrays(target, source, options);
      } else if (TypeCheckerUtil.areAllExpectedType(TypeEnum.MAP, sourceType, targetType)) {
        target = MergeUtil.mergeMaps(target, source, options);
      } else if (TypeCheckerUtil.areAllExpectedType(TypeEnum.SET, sourceType, targetType)) {
        target = MergeUtil.mergeSets(target, source, options);
      } else if (TypeCheckerUtil.areAllExpectedType(TypeEnum.OBJECT, sourceType, targetType)) {
        target = MergeUtil.mergeObjects(target, source, options);
      } else if (!TypeCheckerUtil.isUndefined(source)) {
        target = source;
      }
    }

    return target;
  }

  /**
   * Concatenates two sets together, removing duplicates by value (not by reference).
   *
   * @param {Set<unknown>} set1 - The first set.
   * @param {Set<unknown>} set2 - The second set.
   * @param {DeepMergeOptions} options - Optional options for merging
   * @returns {Set<unknown>} The concatenated set.
   */
  private static mergeSets(
    set1: Set<unknown>,
    set2: Set<unknown>,
    options: Partial<DeepMergeOptions> = {}
  ): Set<unknown> {
    options = { ...this.DEFAULT_MERGE_OPTIONS, ...options };
    if (!options.deepMergeSets) return new Set(set2);

    const result: Set<unknown> = new Set(set1);
    for (const item of set2) {
      // Check if the current item from set2 has an identical value in "results". If not, add it.
      if (![...result].some((existingItem) => Utils.isEqual(existingItem, item))) {
        result.add(item);
      }
    }
    return result;
  }

  /**
   * Concatenates two maps together, removing duplicates by value (not by reference).
   *
   * @param {Map<unknown, unknown>} map1 - The first map.
   * @param {Map<unknown, unknown>} map2 - The second map.
   * @param {DeepMergeOptions} options - Optional options for merging
   * @returns {Map<unknown, unknown>} The concatenated map.
   */
  private static mergeMaps(
    map1: Map<unknown, unknown>,
    map2: Map<unknown, unknown>,
    options: Partial<DeepMergeOptions> = {}
  ): Map<unknown, unknown> {
    options = { ...this.DEFAULT_MERGE_OPTIONS, ...options };
    if (!options.deepMergeMaps) return new Map(map2);

    const resultMap: Map<unknown, unknown> = new Map(map1);
    for (const [key, value] of map2) {
      const existingKey = [...resultMap.keys()].find((k) => Utils.isEqual(k, key));
      if (existingKey === undefined) {
        // Set the value if the key does not exist yet
        resultMap.set(key, value);
      } else {
        // Continue deep merging the existing value with the new value and update the map afterwards
        const existingValue = resultMap.get(existingKey);
        const mergedValue = MergeUtil.deepMergeWithOptions(options, existingValue, value);
        resultMap.set(existingKey, mergedValue);
      }
    }
    return resultMap;
  }

  /**
   * Concatenates two arrays together, merging objects at the same indices. In case of non-primitive elements, the
   * objects are merged recursively using {@link deepMerge}.
   *
   * @param {unknown[]} array1 - The first array.
   * @param {unknown[]} array2 - The second array.
   * @param {DeepMergeOptions} options - Optional options for merging
   * @returns {unknown[]} The concatenated array.
   */
  private static mergeArrays(array1: unknown[], array2: unknown[], options: Partial<DeepMergeOptions> = {}): unknown[] {
    options = { ...this.DEFAULT_MERGE_OPTIONS, ...options };
    if (!options.deepMergeArrays) return array2;

    const maxLength: number = Math.max(array1.length, array2.length);
    return Array.from({ length: maxLength }, (_, index) => {
      // Deeply merge all array items element-wise (and only return non-nullish values)
      const merged = MergeUtil.deepMergeWithOptions(options, array1[index], array2[index]);
      return TypeCheckerUtil.isNullish(merged) ? undefined : merged;
    });
  }

  /**
   * Merges two objects together, with values from the second object overriding those in the first.
   *
   * @param {Record<string, unknown>} object1 - The first object.
   * @param {Record<string, unknown>} object2 - The second object.
   * @param {DeepMergeOptions} options - Optional options for merging
   * @returns {Record<string, unknown>} The merged object.
   */
  private static mergeObjects(
    object1: Record<string, unknown>,
    object2: Record<string, unknown>,
    options: Partial<DeepMergeOptions> = {}
  ): Record<string, unknown> {
    options = { ...this.DEFAULT_MERGE_OPTIONS, ...options };
    if (!options.deepMergeObjects) return object2;

    const target: Record<string, unknown> = { ...object1 };
    for (const key in object2) {
      target[key] = MergeUtil.deepMergeWithOptions(options, target[key], object2[key]);
    }
    return target;
  }
}
