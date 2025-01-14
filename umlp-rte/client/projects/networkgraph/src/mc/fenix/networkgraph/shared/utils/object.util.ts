import { TypeCheckerUtil } from "./type-checker.util";
import { ComparisonFunction, ObjectType, SortingMode } from "./types";
import { compareValues } from "./comparison.util";
import { CleanUtil } from "./clean.util";

/**
 * The ObjectUtils class provides utility methods for object manipulation. It provides the following static methods:
 *
 * @example
 *   // Converts an object into a Map.
 *   ConvertObjectToMap();
 *   // Returns an array of the object's keys sorted based on the value of a specified property.
 *   GetObjectKeysSortedByProperty();
 *   // Resolves a field or a chain of fields from an object. The field can be a string, a function, or a chain of
 *   fields denoted by dot notation. ResolveFieldData();
 *   // Resolves a single property from an object.
 *   ResolveProperty();
 *   // Resolves a chain of properties from an object.
 *   ResolvePropertyChain();
 */
export class ObjectUtil {
  /**
   * Convert Object into a Map.
   *
   * @template K - Type of the object keys
   * @template V - Type of the object values
   * @param {Record<K, V>} object - The Object to be converted into a Map.
   * @returns {Map<K, V>} The converted map from the object.
   */
  public static convertObjectToMap<K extends PropertyKey, V>(object: Record<K, V>): Map<K, V> {
    // Convert Map entries to an array, reduce them into an object
    return new Map(Object.entries(object)) as Map<K, V>;
  }

  /**
   * Receives an object and a property name as input, and returns an array of the object's keys sorted in ascending
   * order based on the values of the specified property of the object's values.
   *
   * This function expects that the object's values are also objects and the specified property exists in these objects.
   *
   * Note: "Nullish" (null, undefined, or empty) entries are removed before sorting.
   *
   * @template T - Type of the input object, expected to be an object of objects
   * @template K - Type of the keys of the object
   * @template P - Type of the property in the values of T by which the keys will be sorted, expected
   * @param {T} object - The object to sort, which should have objects as values
   * @param {P} propertyName - The name of the property in the values of T to use for sorting the keys
   * @param {SortingMode} mode - The sorting mode. Default is 'ASC' (ascending). Can be set to 'DESC' for descending
   *   order.
   * @param {ComparisonFunction<T[K][P]>} compareFunction - An optional custom compare function.
   * @returns {(keyof T)[]} An array of the object's keys sorted in ascending order based on the values of the
   *   propertyName in the object's value objects
   */
  public static getObjectKeysSortedByProperty<T extends Record<K, T[K]>, K extends keyof T, P extends keyof T[K]>(
    object: T,
    propertyName: P,
    mode: SortingMode = SortingMode.ASC,
    compareFunction?: ComparisonFunction<T[K][P]>
  ): Array<K> {
    // Remove empty and nullish entries
    object = CleanUtil.cleanDeep(object);

    // Check if every entry has the specified property
    if (!ObjectUtil.hasObjectsWithProperty(object, propertyName)) {
      throw new Error(`Object's values are not objects or do not contain property '${String(propertyName)}'`);
    }

    return (Object.keys(object) as Array<K>).sort((a: K, b: K): number =>
      compareValues(object[a][propertyName], object[b][propertyName], mode, compareFunction)
    );
  }

  /**
   * This function is used to resolve a field or a chain of fields from an object. The field can be a string, a
   * function, or a chain of fields denoted by dot notation. If the field is a function, the function will be invoked
   * with the object as its argument and its result will be returned. If the field does not exist in the object, an
   * error will be thrown. *
   *
   * @example
   *   // simple property
   *   const data1 = { prop1: "hello" };
   *   console.log(ObjectUtils.resolveFieldData(data1, "prop1")); // "hello"
   *
   *   // nested property
   *   const data2 = { prop1: { prop2: "world" } };
   *   console.log(ObjectUtils.resolveFieldData(data2, "prop1.prop2")); // "world"
   *
   *   // function field
   *   const data3 = { prop1: "hello" };
   *   console.log(ObjectUtils.resolveFieldData(data3, (data: any) => data.prop1.toUpperCase())); // "HELLO"
   *
   * @template T - The type of the object.
   * @param {T} data - The object from which to resolve the field.
   * @param {PropertyKey | CallableFunction} field - The field to resolve from the object. Can be a string, a function,
   *   or a chain of fields.
   * @returns {any} - The resolved field data.
   * @throws {TypeError} - If the field is not a string or a function.
   */
  public static resolveFieldData<T extends ObjectType>(data: T, field: PropertyKey | CallableFunction): any {
    if (TypeCheckerUtil.isNullOrUndefined(data) || TypeCheckerUtil.isNullOrUndefined(field)) {
      return null;
    }

    if (TypeCheckerUtil.isFunction(field)) {
      return field(data);
    }

    if (TypeCheckerUtil.isString(field)) {
      return field.includes(".")
        ? ObjectUtil.resolvePropertyChain(data, field)
        : ObjectUtil.resolveProperty(data, field);
    } else {
      throw new TypeError(`Field must be a function or a string.`);
    }
  }

  /**
   * Concatenates an array of given properties using a "." delimiter to create a property chain. Nullish (null or
   * undefined) properties are ignored during the concatenation.
   *
   * @param {string | undefined | null} properties - An array of properties to be concatenated. Nullish values will be
   *   ignored.
   * @returns {string} - The concatenated property chain as a string.
   */
  public static createPropertyChain(...properties: (string | undefined | null)[]): string {
    // Instead of using ".join" we do it manually for performance increase
    let result: string = "";
    for (let index = 0, length = properties.length; index < length; index++) {
      const property: string | null | undefined = properties[index];
      if (property) {
        if (result) result += ".";
        result += property;
      }
    }
    return result;
  }

  /**
   * Determines if the passed value is an object and contains all the specified properties.
   *
   * @template T - The type of properties, which can be either of PropertyKey or a Function.
   * @param {unknown} value - The object to check.
   * @param {T[]} properties - An array of properties to verify in the object.
   * @returns {value is { [K in PropertyKey]: unknown }} - Boolean indicating whether the value is an object containing
   *   all specified properties.
   */
  public static hasProperties<T extends PropertyKey | CallableFunction>(
    value: unknown,
    ...properties: T[]
  ): value is { [K in PropertyKey]: unknown } {
    if (typeof value !== "object" || value === null) return false;

    return properties.every((property: T): boolean => {
      return TypeCheckerUtil.isFunction(property) ? property(value) : (property as PropertyKey) in value;
    });
  }

  /**
   * Determines if the passed value is an object where all its own properties are objects that contain the specified
   * property key.
   *
   * @template T - The type of the properties within each object.
   * @template K - The type of the property key to check for.
   * @param {unknown} value - The object to check.
   * @param {K} property - The property key to verify within each nested object.
   * @returns {value is Record<PropertyKey, Record<K, T>>} - Boolean indicating whether the value contains objects that
   *   each have the specified property.
   */
  public static hasObjectsWithProperty<T, K extends PropertyKey>(
    value: unknown,
    property: K
  ): value is Record<PropertyKey, Record<K, T>> {
    return (
      TypeCheckerUtil.isObject(value) && Object.values(value).every((item) => ObjectUtil.hasProperties(item, property))
    );
  }

  /**
   * Utility function that resolves the given property. If the field does not exist in the object, an error will be
   * thrown.
   *
   * @example
   *   // simple property
   *   const data1 = { prop1: "hello" };
   *   console.log(ObjectUtils.resolveFieldData(data1, "prop1")); // "hello"
   *
   * @template T - The type of the object.
   * @template K - The type of the object's keys.
   * @param {T} data - The object from which to resolve the property.
   * @param {string} property - The property to resolve from the object.
   * @returns {any} - The resolved property data.
   * @throws {Error} - If the property does not exist on the data object.
   */
  private static resolveProperty<T extends ObjectType<V>, K extends keyof T, V>(data: T, property: K): V {
    if (ObjectUtil.hasProperties(data, property)) {
      return data[property];
    } else {
      throw new Error(`Property '${String(property)}' does not exist on the data object.`);
    }
  }

  /**
   * Utility function that resolves the given property chain (multiple properties separated with a ".").
   *
   * @example
   *   // nested property
   *   const data2 = { prop1: { prop2: "world" } };
   *   console.log(ObjectUtils.resolveFieldData(data2, "prop1.prop2")); // "world"
   *
   * @template T - The type of the object.
   * @param {T} data - The object from which to resolve the chain of properties.
   * @param {string} properties - The chain of properties to resolve from the object.
   * @returns {any} - The resolved property data.
   */
  private static resolvePropertyChain<T extends ObjectType<any>>(data: T, properties: string): any {
    const fields: string[] = properties.split(".");
    let value: any = data;
    for (let index: number = 0, length: number = fields.length; index < length; ++index) {
      value = ObjectUtil.resolveProperty(value, fields[index]);
    }
    return value;
  }
}
