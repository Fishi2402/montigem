import { TypeCheckerUtil } from "./type-checker.util";
import { ComparisonFunction, SortingMode } from "./types";

/**
 * Compares two values of type string, number, or boolean. For strings, it uses the localeCompare function. For numbers
 * and booleans, it uses the subtraction operator.
 *
 * @param {unknown} value1 - The first value to compare.
 * @param {unknown} value2 - The second value to compare.
 * @returns {number} - A negative number if "value1" comes after "value2" (swapping needed), zero if "value1" and
 *   "value2" are identical in ordering, or a positive number if "value1" comes before "value2".
 * @throws {Error} - If the values are not of type string, number, or boolean, an error is thrown.
 */
function defaultCompareFunction(value1: unknown, value2: unknown): number {
  if (TypeCheckerUtil.isString(value1) && TypeCheckerUtil.isString(value2)) {
    return value1.localeCompare(value2);
  } else if (
    (TypeCheckerUtil.isNumber(value1) && TypeCheckerUtil.isNumber(value2)) ||
    (TypeCheckerUtil.isBoolean(value1) && TypeCheckerUtil.isBoolean(value2))
  ) {
    return Number(value1) - Number(value2);
  }

  throw new Error(`Cannot compare values of type ${typeof value1} and ${typeof value2}`);
}

/**
 * This function compares two values and returns a number indicating whether one value is less than, equal to, or
 * greater than the other. The comparison is made according to a specified sorting mode and an optional custom compare
 * function.
 *
 * @template T - The type of the values to compare.
 * @param {T} value1 - The first value to compare.
 * @param {T} value2 - The second value to compare.
 * @param {SortingMode} mode - The sorting mode. Default is 'ASC' (ascending). Can be set to 'DESC' for descending
 *   order.
 * @param {ComparisonFunction<T>} compareFunction - An optional custom compare function.
 * @returns {number} - A negative number if "a" is less than "b", zero if "a" equals "b", or "a" positive number if "a"
 *   is greater than "b".
 * @throws Error if the type of values is not comparable (neither string, nor number, nor comparableFunction).
 */
export function compareValues<T>(
  value1: T,
  value2: T,
  mode: SortingMode = SortingMode.ASC,
  compareFunction?: ComparisonFunction<T>
): number {
  if (TypeCheckerUtil.isNullish(value1) || TypeCheckerUtil.isNullish(value2)) {
    return TypeCheckerUtil.isNullish(value1) && TypeCheckerUtil.isNullish(value2)
      ? 0
      : TypeCheckerUtil.isNullish(value1)
        ? -1
        : 1;
  }

  const comparator: ComparisonFunction<T> = compareFunction ?? defaultCompareFunction;
  const comparison: number = comparator(value1, value2);

  return mode === SortingMode.ASC || comparison === 0 ? comparison : -comparison;
}
