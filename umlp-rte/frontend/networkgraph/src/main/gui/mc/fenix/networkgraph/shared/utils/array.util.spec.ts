import { ArrayType, SortingMode } from "./types";
import { ArrayUtil } from "./array.util";

describe("ArrayUtils", () => {
  // Test for sorting of arrays of objects by a certain property
  describe("sortArrayByProperty", () => {
    it("should correctly sort an array of objects by property in ascending order", () => {
      const array: ArrayType<{
        prop: number;
      }> = [{ prop: 3 }, { prop: 1 }, { prop: 4 }];
      const result: ArrayType = ArrayUtil.sortArrayByProperty(array, "prop");
      const expected: ArrayType = [{ prop: 1 }, { prop: 3 }, { prop: 4 }];
      expect(result).toEqual(expected);
    });

    it("should handle sorting modes correctly", () => {
      const array: ArrayType<{
        prop: number;
      }> = [{ prop: 3 }, { prop: 1 }, { prop: 4 }];
      const result: ArrayType = ArrayUtil.sortArrayByProperty(array, "prop", SortingMode.DESC);
      const expected: ArrayType = [{ prop: 4 }, { prop: 3 }, { prop: 1 }];
      expect(result).toEqual(expected);
    });

    it("should remove undefined values correctly", () => {
      const array: ArrayType<{
        prop: number | undefined;
      }> = [{ prop: 5 }, { prop: undefined }, { prop: 3 }];
      const result: ArrayType = ArrayUtil.sortArrayByProperty(array, "prop");
      const expected: ArrayType = [
        {
          prop: 3,
        },
        {
          prop: 5,
        },
      ];
      expect(result).toEqual(expected);
    });

    it("should handle nested undefined values correctly", () => {
      const array: ArrayType<{
        prop?: unknown;
        value?: unknown;
      }> = [
        { prop: 5, value: undefined },
        { prop: 3, value: undefined },
        { prop: 4, value: "test" },
      ];
      const result: ArrayType = ArrayUtil.sortArrayByProperty(array, "prop");
      const expected: ArrayType = [{ prop: 3 }, { prop: 4, value: "test" }, { prop: 5 }];
      expect(result).toEqual(expected);
    });

    it("should throw error when property values have different types", () => {
      const array: ArrayType<{
        prop: unknown;
      }> = [{ prop: 5 }, { prop: 3 }, { prop: "4" }];
      expect(() => ArrayUtil.sortArrayByProperty(array, "prop")).toThrow();
    });

    it("should throw error on nested objects", () => {
      const array: ArrayType<{
        prop?: unknown;
        value?: unknown;
      }> = [{ value: { prop: undefined } }, { value: { prop: undefined } }, { value: { prop: "test" } }];
      expect(() => ArrayUtil.sortArrayByProperty(array, "prop")).toThrow();
    });

    it("should throw error if property not found", () => {
      const array: ArrayType<{
        prop: number;
      }> = [{ prop: 3 }, { prop: 1 }, { prop: 4 }];
      expect(() => ArrayUtil.sortArrayByProperty(array, "nonexistent" as never)).toThrow();
    });

    it("should throw error when sorting an array of numbers", () => {
      const array: ArrayType = [3, 1, 4];
      expect(() => ArrayUtil.sortArrayByProperty(array as never, "nonExistentProp" as never)).toThrow();
    });

    it("should throw error when some objects do not have the property", () => {
      const array: ArrayType<{
        prop?: number;
      }> = [{ prop: 3 }, { prop: 1 }, {}];
      const result: ArrayType = ArrayUtil.sortArrayByProperty(array, "prop");
      const expected: ArrayType = [{ prop: 1 }, { prop: 3 }];
      expect(result).toEqual(expected);
    });
  });

  // Test for sorting primitive arrays
  describe("sortPrimitiveArray", () => {
    it("should return an empty array when the input is an empty array", () => {
      const array: ArrayType<string> = [];
      expect(ArrayUtil.sortPrimitiveArray(array)).toEqual([]);
    });

    it("should correctly sort a number array in ascending order", () => {
      const array: ArrayType<number> = [3, 1, 4, 2];
      const result: ArrayType<number> = ArrayUtil.sortPrimitiveArray(array);
      expect(result).toEqual([1, 2, 3, 4]);
    });

    it("should correctly sort a string array in descending order", () => {
      const array: ArrayType<string> = ["c", "a", "b"];
      const result: ArrayType<string> = ArrayUtil.sortPrimitiveArray(array, SortingMode.DESC);
      expect(result).toEqual(["c", "b", "a"]);
    });

    it("should correctly sort a boolean array in ascending order", () => {
      const array: ArrayType<boolean> = [false, true, false];
      const result: ArrayType<boolean> = ArrayUtil.sortPrimitiveArray(array);
      expect(result).toEqual([false, false, true]);
    });

    it("should throw an error when array contains mixed types", () => {
      const array: ArrayType<any> = [1, "2", true, undefined, null];
      expect(() => ArrayUtil.sortPrimitiveArray(array)).toThrow();
    });

    it("should correctly sort an array with a custom compare function", () => {
      const array: ArrayType<number> = [3, 1, 4, 2];
      const compareFunction = (a: number, b: number) => b - a; // DESC
      const result: ArrayType<number> = ArrayUtil.sortPrimitiveArray(array, SortingMode.ASC, compareFunction);
      expect(result).toEqual([4, 3, 2, 1]); // DESC due to the custom compare function
    });

    it("should remove undefined values correctly", () => {
      const array: ArrayType<number | undefined | null> = [3, undefined, 4, 2, null];
      const result: ArrayType<number | undefined | null> = ArrayUtil.sortPrimitiveArray(array);
      expect(result).toEqual([2, 3, 4]);
    });
  });

  describe("hasObjectsWithProperty", () => {
    it("should return true for an array of objects that all have the specified property", () => {
      expect(ArrayUtil.hasObjectsWithProperty([{ a: 1 }, { a: 2 }], "a")).toBe(true);
    });

    it("should return false for an array of objects where at least one does not have the specified property", () => {
      expect(ArrayUtil.hasObjectsWithProperty([{ a: 1 }, { b: 2 }], "a")).toBe(false);
    });

    it("should return false for an empty array", () => {
      expect(ArrayUtil.hasObjectsWithProperty([], "a")).toBe(false);
    });
  });

  describe("flatten function", () => {
    it("should flatten nested arrays", () => {
      const input = [1, 2, 3, [4, [5, [6, [7]]]]];
      const result = ArrayUtil.flatten(input);
      expect(result).toEqual([1, 2, 3, 4, 5, 6, 7]);
    });

    it("should handle already flat arrays", () => {
      const input = [1, 2, 3, 4, 5];
      const result = ArrayUtil.flatten(input);
      expect(result).toEqual([1, 2, 3, 4, 5]);
    });

    it("should handle empty arrays", () => {
      const input: any[] = [];
      const result = ArrayUtil.flatten(input);
      expect(result).toEqual([]);
    });

    it("should handle arrays containing mixed data types", () => {
      const input = [1, "a", [2, "b", [3, "c"], "d"], "e"];
      const result = ArrayUtil.flatten(input);
      expect(result).toEqual([1, "a", 2, "b", 3, "c", "d", "e"]);
    });

    it("should handle arrays containing objects", () => {
      const input = [
        { a: 1, e: [] },
        { a: 2, e: [] },
        [
          { a: 3, e: [] },
          { a: 4, e: [{ f: 5 }] },
        ],
      ];
      const result = ArrayUtil.flatten(input);
      expect(result).toEqual([
        { a: 1, e: [] },
        { a: 2, e: [] },
        { a: 3, e: [] },
        { a: 4, e: [{ f: 5 }] },
      ]);
    });
  });
});
