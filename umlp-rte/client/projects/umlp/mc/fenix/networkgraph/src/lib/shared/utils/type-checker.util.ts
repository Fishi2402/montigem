import { EventEmitter } from "@angular/core";
import { ArrayType, FunctionType, ObjectType, TypeCheckerFunction } from "./types";
import { DateUtil } from "./date.util";

/**
 * An enumeration of all types that can be checked by the TypeChecker class. This includes primitive types like String,
 * Number, Boolean, as well as complex types like Date, Array, Map, Set, etc.
 */
export enum TypeEnum {
  DATE = "Date",
  STRING = "String",
  NUMBER = "Number",
  BOOLEAN = "Boolean",
  UNDEFINED = "Undefined",
  NULL = "Null",
  SYMBOL = "Symbol",
  ARRAY = "Array",
  REG_EXP = "RegExp",
  MAP = "Map",
  SET = "Set",
  CLASS = "Class",
  CLASS_INSTANCE = "ClassInstance",
  FUNCTION = "Function",
  OBJECT = "Object",
  UNKNOWN = "Unknown",
}

/**
 * Utility class that provides various type checking methods. These methods can be used to determine if a variable is of
 * a certain type, such as a string, number, boolean, etc. In addition to the basic JavaScript types, it also handles
 * special types like Date, RegExp, Map, Set, etc., and even checks for class instances. It also provides methods to
 * check if a variable is a valid property key, or if it's nullish.
 */
export class TypeCheckerUtil {
  /**
   * A Map of primitive types to their respective checking functions. This is used internally by other methods in the
   * TypeChecker class to check if a value is of a primitive type.
   */
  private static readonly PRIMITIVE_TYPE_CHECKERS: Partial<{ [key in TypeEnum]: TypeCheckerFunction }> = {
    [TypeEnum.STRING]: TypeCheckerUtil.isString,
    [TypeEnum.NUMBER]: TypeCheckerUtil.isNumber,
    [TypeEnum.BOOLEAN]: TypeCheckerUtil.isBoolean,
    [TypeEnum.NULL]: TypeCheckerUtil.isNull,
    [TypeEnum.UNDEFINED]: TypeCheckerUtil.isUndefined,
    [TypeEnum.SYMBOL]: TypeCheckerUtil.isSymbol,
  };

  /**
   * A Map of all TypeEnum values to their respective checking functions. This is used internally by other methods in
   * the TypeChecker class to get the type of value.
   */
  private static readonly TYPE_CHECKERS: Partial<{ [key in TypeEnum]: TypeCheckerFunction }> = {
    [TypeEnum.DATE]: TypeCheckerUtil.isDate, // Note that we need to check "isDate" before "isString" to avoid conflicts
    ...TypeCheckerUtil.PRIMITIVE_TYPE_CHECKERS,
    [TypeEnum.SET]: TypeCheckerUtil.isSet,
    [TypeEnum.ARRAY]: TypeCheckerUtil.isArray,
    [TypeEnum.REG_EXP]: TypeCheckerUtil.isRegExp,
    [TypeEnum.MAP]: TypeCheckerUtil.isMap,
    [TypeEnum.CLASS]: TypeCheckerUtil.isClass,
    [TypeEnum.CLASS_INSTANCE]: TypeCheckerUtil.isClassInstance,
    [TypeEnum.FUNCTION]: TypeCheckerUtil.isFunction,
    [TypeEnum.OBJECT]: TypeCheckerUtil.isObject,
  };

  /**
   * A set of common constructors used for efficient type checking whether a value is an object or not.
   *
   * This set contains references to the constructors of commonly used built-in JavaScript objects. Using a Set for
   * these checks can improve performance during type detection, as Set.has() is faster than an Array.some() or similar
   * iterative checks.
   */
  private static readonly COMMON_CONSTRUCTORS: Set<Function> = new Set<Function>([
    Array,
    Date,
    RegExp,
    Set,
    Map,
    Function,
  ]);

  /**
   * Get the type of value.
   *
   * @param {unknown} value - The value to check.
   * @returns {boolean} - The type of the value.
   */
  static getType(value: unknown): TypeEnum {
    const directType = typeof value;
    if (directType in TypeEnum) {
      if (value instanceof Date || (typeof value === "string" && DateUtil.isDate(value))) return TypeEnum.DATE;
      return TypeEnum[directType as keyof typeof TypeEnum];
    }
    for (const [type, checker] of Object.entries(TypeCheckerUtil.TYPE_CHECKERS)) {
      if (checker(value)) return type as TypeEnum;
    }
    return TypeEnum.UNKNOWN;
  }

  /**
   * Determines if a value is a string.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is a string, false otherwise.
   */
  static isString(value: unknown): value is string {
    return typeof value === "string";
  }

  /**
   * Determines if a value is a number.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is a number, false otherwise.
   */
  static isNumber(value: unknown): value is number {
    return typeof value === "number";
  }

  /**
   * Determines if a value is a boolean.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is a boolean, false otherwise.
   */
  static isBoolean(value: unknown): value is boolean {
    return typeof value === "boolean";
  }

  /**
   * Determines if a value is a function.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is a function, false otherwise.
   */
  static isFunction(value: unknown): value is FunctionType {
    return typeof value === "function" && !!(value && value.constructor && value.apply);
  }

  /**
   * Determines if a value is a symbol.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is a symbol, false otherwise.
   */
  static isSymbol(value: unknown): value is symbol {
    return typeof value === "symbol";
  }

  /**
   * Determines if a value is null or undefined.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is null or undefined, false otherwise.
   */
  static isNullOrUndefined(value: unknown): value is null | undefined {
    return value == null;
  }

  /**
   * Determines if a value is null.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is null, false otherwise.
   */
  static isNull(value: unknown): value is null {
    return value === null;
  }

  /**
   * Determines if a value is undefined.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is undefined, false otherwise.
   */
  static isUndefined(value: unknown): value is undefined {
    return value === undefined;
  }

  /**
   * Determines if a value is an array.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is an array, false otherwise.
   */
  static isArray<T>(value: unknown): value is Array<T> {
    return Array.isArray(value);
  }

  /**
   * Determines if a value is a map.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is a map, false otherwise.
   */
  static isMap<K, V>(value: unknown): value is Map<K, V> {
    return value instanceof Map;
  }

  /**
   * Determines if a value is a set.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is a set, false otherwise.
   */
  static isSet<T>(value: unknown): value is Set<T> {
    return value instanceof Set;
  }

  /**
   * Determines if a value is an event emitter.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is an event emitter, false otherwise.
   */
  static isEventEmitter<T>(value: unknown): value is EventEmitter<T> {
    return value instanceof EventEmitter;
  }

  /**
   * Determines if a value is a regular expression.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is a regular expression, false otherwise.
   */
  static isRegExp(value: unknown): value is RegExp {
    return value instanceof RegExp;
  }

  /**
   * Determines if a value is a class instance.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value is a class instance, false otherwise.
   */
  static isClassInstance(value: unknown): boolean {
    return typeof value === "object" && !!value?.constructor && value.constructor !== Object;
  }

  /**
   * Determines if a value represents a class.
   *
   * @param {unknown} value - The value to analyze.
   * @returns {boolean} - True if the value represents a class, false otherwise.
   */
  static isClass(value: unknown): boolean {
    return typeof value === "function" && /^class\s/.test(Function.prototype.toString.call(value));
  }

  /**
   * Checks if a value is a Date or a string that can be parsed into a Date.
   *
   * @param {unknown} value - The value to check.
   * @returns {boolean} - True if the value is a Date or a parseable string, false otherwise.
   */
  static isDate(value: unknown): value is Date | string {
    return DateUtil.isDate(value);
  }

  /**
   * Checks if a value is nullish, i.e. if it's null, undefined, an empty array, a Set with no elements, a Map with no
   * entries, an object with no properties, or an empty string.
   *
   * @param {unknown} value - The value to check.
   * @returns {boolean} - True if the value is nullish, false otherwise.
   */
  static isNullish(value: unknown): value is null | undefined {
    return (
      value == null ||
      (Array.isArray(value) && value.length === 0) ||
      (value instanceof Set && value.size === 0) ||
      (value instanceof Map && value.size === 0) ||
      (TypeCheckerUtil.isObject(value) && Object.keys(value).length === 0) ||
      (typeof value === "string" && value === "")
    );
  }

  /**
   * Helper method to check if a given value is of the specified type.
   *
   * @param {TypeEnum} type - The type to check for.
   * @param {unknown} value - The value to check against the specified type.
   * @returns {boolean} - True if the value is of the specified type, false otherwise.
   */
  static isOfType(type: TypeEnum, value: unknown): boolean {
    return TypeCheckerUtil.getType(value) === type;
  }

  /**
   * Helper method to assert if the given `actualType` is the same as any of the expected types.
   *
   * @param {TypeEnum} actualType - The actual type of the value.
   * @param {...TypeEnum[]} expectedTypes - The expected types to compare against.
   * @returns {boolean} - True if the actual type is the same as any of the expected types, false otherwise.
   */
  static isExpectedType(actualType: TypeEnum, ...expectedTypes: TypeEnum[]): boolean {
    return expectedTypes.includes(actualType);
  }

  /**
   * Check if a value is a primitive type.
   *
   * @param {unknown} value - The value to check.
   * @returns {boolean} - True if the value is a primitive type, false otherwise.
   */
  public static isPrimitive(value: unknown): boolean {
    return TypeCheckerUtil.PRIMITIVE_TYPE_CHECKERS[TypeCheckerUtil.getType(value)] != null;
  }

  /**
   * Check if a value is a primitive type.
   *
   * @param {ArrayType} value - The value to check.
   * @returns {boolean} - True if the value is a primitive type, false otherwise.
   */
  static isArrayOfPrimitives(value: ArrayType): boolean {
    return value.every((a: unknown) => TypeCheckerUtil.isPrimitive(a));
  }

  /**
   * Check if an object only has primitive types as values.
   *
   * @param {ObjectType} object - The object to check.
   * @returns {boolean} - True if all values of the object are of primitive types, false otherwise.
   */
  public static isObjectOfPrimitives(object: ObjectType): boolean {
    return Object.values(object).every((value: unknown) => TypeCheckerUtil.isPrimitive(value));
  }

  /**
   * Check if the passed type is a primitive type.
   *
   * @param {TypeEnum} type - The type to check.
   * @returns {boolean} - True if the type value is a primitive type, false otherwise.
   */
  public static isTypePrimitive(type: TypeEnum | undefined): boolean {
    return type != null && TypeCheckerUtil.PRIMITIVE_TYPE_CHECKERS[type] != null;
  }

  /**
   * Check if a value is an object (not including arrays, dates, and regular expressions).
   *
   * @template T - The type of the object.
   * @param {unknown} value - The value to check.
   * @returns {boolean} - True if the value is an object, false otherwise.
   */
  static isObject<T>(value: unknown): value is Record<keyof T, T[keyof T]> {
    return (
      typeof value === "object" &&
      value != null &&
      !TypeCheckerUtil.COMMON_CONSTRUCTORS.has(value.constructor) &&
      value.constructor === Object // Rule out instances of classes
    );
  }

  /**
   * Check if a value is a valid property key, that is, either string, number, or symbol.
   *
   * @param {unknown} value - Value to check
   * @returns {boolean} - True if the value is a PropertyKey, false otherwise.
   */
  public static isPropertyKey(value: unknown): value is PropertyKey {
    return TypeCheckerUtil.isString(value) || TypeCheckerUtil.isNumber(value) || TypeCheckerUtil.isSymbol(value);
  }

  /**
   * Helper method to assert if the given `expectedType` is the same as all the `actualTypes`
   *
   * @param {TypeEnum} expectedType - The expected type to compare against.
   * @param {...TypeEnum[]} actualTypes - The actual types to compare.
   * @returns {boolean} - True if the all actual types are the same as the expected type, false otherwise.
   */
  static areAllExpectedType(expectedType: TypeEnum, ...actualTypes: TypeEnum[]): boolean {
    return actualTypes.every((type: TypeEnum) => expectedType === type);
  }

  /**
   * Check if all passed elements are of primitive type.
   *
   * @param {unknown} values - The values to check.
   * @returns {boolean} - True if all values are of primitive type, false otherwise.
   */
  public static areAllPrimitives(...values: ArrayType): boolean {
    return values.every((value: unknown) => TypeCheckerUtil.isPrimitive(value));
  }

  /**
   * Checks if all elements (values) in an object are primitive types.
   *
   * @param {unknown} object - The object to check.
   * @returns {boolean} - True if all elements in the object are primitive types, false otherwise.
   */
  public static areAllObjectValuesPrimitive(object: ObjectType): boolean {
    return Object.values(object).every((value: unknown) => this.isPrimitive(value));
  }

  /**
   * Helper method to check if all passed elements are of the passed type (`type`).
   *
   * @remarks
   *   Nullish values (i.e., empty arrays, "undefined", ...) are ignored.
   * @param {TypeEnum} type - Type to check for
   * @param {ArrayType} values - The values to check.
   * @returns {boolean} - True if all values are of the passed type (`type`), false otherwise.
   */
  static areAllOfType(type: TypeEnum, ...values: ArrayType): boolean {
    return values.every((value) => TypeCheckerUtil.getType(value) === type || TypeCheckerUtil.isNullish(value));
  }
}
