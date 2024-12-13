import { TypeCheckerUtil } from "./type-checker.util";

/**
 * JsonUtils is a utility class that provides methods for converting objects to JSON strings and vice versa, with
 * special handling for JavaScript types like Map and Set which are not natively handled by JSON.stringify and
 * JSON.parse.
 */
export class JsonUtil {
  /**
   * Converts an object to its JSON string representation.
   *
   * @param {any} object - The object to be converted.
   * @returns {string} - The JSON string representation of the object.
   */
  public static convertToJson(object: any): string {
    return JSON.stringify(object, this.jsonReplacer, 2);
  }

  /**
   * Converts a JSON string back to its original object representation.
   *
   * @param {string} jsonString - The JSON string to be converted.
   * @returns {any} - The reconstructed object.
   */
  public static convertFromJson(jsonString: string): any {
    return JSON.parse(jsonString, this.jsonReviver);
  }

  /**
   * Replacer function for JSON.stringify to handle special types like Map and Set.
   *
   * @param {string} key - The current key being processed.
   * @param {any} value - The current value being processed.
   * @returns {any} - The replaced value.
   */
  private static jsonReplacer(key: string, value: any): any {
    if (TypeCheckerUtil.isMap(value)) {
      return {
        dataType: "Map",
        value: [...value.entries()],
      };
    } else if (TypeCheckerUtil.isSet(value)) {
      return {
        dataType: "Set",
        value: [...value.values()],
      };
    } else {
      return value;
    }
  }

  /**
   * Reviver function for JSON.parse to reconstruct special types like Map and Set.
   *
   * @param {string} key - The current key being processed.
   * @param {any} value - The current value being processed.
   * @returns {any} - The reconstructed value.
   */
  private static jsonReviver(key: string, value: any): any {
    if (value && value.dataType === "Map") {
      return new Map(value.value);
    } else if (value && value.dataType === "Set") {
      return new Set(value.value);
    } else {
      return value;
    }
  }
}
