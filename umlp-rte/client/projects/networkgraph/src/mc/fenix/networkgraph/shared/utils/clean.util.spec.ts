import { ArrayType, MapType, ObjectType } from "./types";
import { CleanUtil } from "./clean.util";

describe("Clean Deep", () => {
  // General Tests
  describe("General Clean-Deep Testing", () => {
    // Test with non-object, non-array type
    it("should return the value as is when it is not an object or an array", () => {
      const value: string = "test";
      expect(CleanUtil.cleanDeep(value)).toEqual("test");
    });

    // Test with array
    it("should return an array with null and undefined values removed", () => {
      const array: ArrayType = [1, null, 2, undefined, 3, null];
      expect(CleanUtil.cleanDeep(array)).toEqual([1, 2, 3]);
    });

    // Test with map
    it("should return a map with null and undefined values removed", () => {
      const array: MapType = new Map([
        [1, [null, undefined]],
        [2, { a: undefined, b: 1 }],
        [3, { a: null, b: undefined, c: 3 }],
      ]);
      expect(CleanUtil.cleanDeep(array)).toEqual(
        new Map([
          [2, { b: 1 }],

          [3, { c: 3 }],
        ])
      );
    });

    // Test with nested array
    it("should return a nested array with null and undefined values removed", () => {
      const array: ArrayType = [1, [null, 2, undefined, [3, null]]];
      expect(CleanUtil.cleanDeep(array)).toEqual([1, [2, [3]]]);
    });

    // Test with object
    it("should return an object with properties with null and undefined values removed", () => {
      const object: ObjectType = {
        a: 1,
        b: null,
        c: 2,
        d: undefined,
        e: 3,
      };
      expect(CleanUtil.cleanDeep(object)).toEqual({
        a: 1,
        c: 2,
        e: 3,
      });
    });

    // Test with nested object
    it("should return a nested object with properties with null and undefined values removed", () => {
      const object: ObjectType = {
        a: 1,
        b: {
          bb: null,
          cc: 2,
          dd: { ddd: undefined, eee: 3 },
        },
      };
      expect(CleanUtil.cleanDeep(object)).toEqual({
        a: 1,
        b: { cc: 2, dd: { eee: 3 } },
      });
    });

    // Test with empty array
    it("should return an empty array when the input is an empty array", () => {
      const array: ArrayType = [];
      expect(CleanUtil.cleanDeep(array)).toEqual([]);
    });

    // Test with empty object
    it("should return an empty object when the input is an empty object", () => {
      const object: ObjectType = {};
      expect(CleanUtil.cleanDeep(object)).toEqual({});
    });

    it("should return an empty object when the input is an nested object with nulls and undefined", () => {
      const array: ArrayType = [
        [[], []],
        {
          a: [null],
          b: [{ c: null }, undefined],
          d: undefined,
        },
      ];
      const expected: ArrayType = [];
      expect(CleanUtil.cleanDeep(array)).toEqual(expected);
    });
  });

  // Test for the cleaning of arrays
  describe("Detailed Array Cleaning", () => {
    it("should return an array with null and undefined values removed", () => {
      const array: ArrayType = [1, null, 2, undefined, 3, null];
      expect(CleanUtil.cleanDeep(array)).toEqual([1, 2, 3]);
    });

    it("should return a nested array with null and undefined values removed", () => {
      const array: ArrayType = [1, [null, 2, undefined, [3, null]]];
      expect(CleanUtil.cleanDeep(array)).toEqual([1, [2, [3]]]);
    });

    it("should return an empty array when the input is an empty array", () => {
      const array: ArrayType = [];
      expect(CleanUtil.cleanDeep(array)).toEqual([]);
    });

    it("should return an empty array when the input is an nested array with nulls and undefined", () => {
      const array: ArrayType = [[null], [null, undefined], undefined];
      expect(CleanUtil.cleanDeep(array)).toEqual([]);
    });
  });

  // Test for the cleaning of maps
  describe("Detailed Map Cleaning", () => {
    it("should return a map with null and undefined values removed", () => {
      const map: MapType = new Map([
        ["key1", [null, undefined]],
        ["key2", { a: undefined, b: 1 }],
        ["key3", { a: null, b: undefined, c: 3 }],
        ["key4", null],
        ["key5", undefined],
      ]);
      const result: MapType = CleanUtil.cleanDeep(map);
      const expected: MapType = new Map([
        ["key2", { b: 1 }],
        ["key3", { c: 3 }],
      ]);

      expect(result).toEqual(expected);
    });

    it("should return an empty map when all values are null or undefined", () => {
      const map: MapType = new Map([
        ["key1", null],
        ["key2", undefined],
        ["key3", [null, undefined]],
        ["key4", { a: null, b: undefined }],
      ]);
      const result: MapType = CleanUtil.cleanDeep(map);
      const expected: MapType = new Map([]);

      expect(result).toEqual(expected);
    });

    it("should return the same map when no values are null or undefined", () => {
      const map: MapType = new Map();
      map.set("key1", 1);
      map.set("key2", "2");
      map.set("key3", [1, 2, 3]);
      map.set("key4", { a: 1, b: 2, c: 3 });
      const result: MapType = CleanUtil.cleanDeep(map);

      expect(result).toEqual(map);
    });
  });

  // Test for the cleaning of objects
  describe("Detailed Object Cleaning", () => {
    it("should return an object with properties with null and undefined values removed", () => {
      const object: ObjectType = {
        a: 1,
        b: null,
        c: 2,
        d: undefined,
        e: 3,
      };
      const result = CleanUtil.cleanDeep(object);
      const expected = {
        a: 1,
        c: 2,
        e: 3,
      };
      expect(result).toEqual(expected);
    });

    it("should return a nested object with properties with null and undefined values removed", () => {
      const object: ObjectType = {
        a: 1,
        b: {
          bb: null,
          cc: 2,
          dd: { ddd: undefined, eee: 3 },
        },
      };
      const result = CleanUtil.cleanDeep(object);
      const expected = {
        a: 1,
        b: { cc: 2, dd: { eee: 3 } },
      };
      expect(result).toEqual(expected);
    });

    it("should return an empty object when the input is an empty object", () => {
      const object: ObjectType = {};
      const result = CleanUtil.cleanDeep(object);
      const expected = {};
      expect(result).toEqual(expected);
    });

    it("should return an empty object when the input is an nested object with nulls and undefined", () => {
      const object: ObjectType = {
        a: [null],
        b: [{ c: null }, undefined],
        d: undefined,
      };
      const result = CleanUtil.cleanDeep(object);
      const expected = {};
      expect(result).toEqual(expected);
    });
  });
});
