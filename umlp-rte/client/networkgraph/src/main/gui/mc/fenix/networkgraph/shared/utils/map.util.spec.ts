import { ArrayType, MapType, ObjectType, SortingMode } from "./types";

import { MapUtil } from "./map.util";

describe("MapUtils", () => {
  // Test for keys of map sorted by property of object of map
  describe("getMapKeysSortedByProperty", () => {
    it("should correctly sort map keys based on property values", () => {
      const map: MapType<string, { prop: number }> = new Map([
        ["a", { prop: 3 }],
        ["b", { prop: 1 }],
        ["c", { prop: 2 }],
      ]);

      const result: ArrayType = MapUtil.getMapKeysSortedByProperty(map, "prop");
      const expected: ArrayType = ["b", "c", "a"];
      expect(result).toEqual(result);
    });

    it("should handle sorting modes correctly", () => {
      const map: MapType<string, { prop: number }> = new Map([
        ["a", { prop: 3 }],
        ["b", { prop: 1 }],
        ["c", { prop: 2 }],
      ]);

      const result: ArrayType = MapUtil.getMapKeysSortedByProperty(map, "prop", SortingMode.DESC);
      const expected: ArrayType = ["a", "c", "b"];
      expect(result).toEqual(result);
    });

    it("should remove undefined values", () => {
      const map: MapType<string, { prop: number | undefined }> = new Map([
        ["a", { prop: 3 }],
        ["b", { prop: 2 }],
        ["c", { prop: undefined }],
      ]);

      const result: ArrayType = MapUtil.getMapKeysSortedByProperty(map, "prop", SortingMode.ASC);
      const expected: ArrayType = ["b", "a"];
      expect(result).toEqual(result);
    });

    it("should handle complex objects correctly", () => {
      const map: MapType<string, { prop: { value: number } }> = new Map([
        ["a", { prop: { value: 3 } }],
        ["b", { prop: { value: 1 } }],
        ["c", { prop: { value: 2 } }],
      ]);

      const customCompareFunction = (a: { value: number }, b: { value: number }): number => a.value - b.value;

      const result: ArrayType = MapUtil.getMapKeysSortedByProperty(map, "prop", SortingMode.ASC, customCompareFunction);
      const expected: ArrayType = ["b", "c", "a"];
      expect(result).toEqual(result);
    });

    it("should throw an error if property not found", () => {
      const map: MapType<string, { firstProp: number; secondProp: string }> = new Map([
        ["a", { firstProp: 3, secondProp: "Test 1" }],
        ["b", { firstProp: 1, secondProp: "Test 2" }],
        ["c", { firstProp: 2, secondProp: "Test 3" }],
      ]);

      expect(() => MapUtil.getMapKeysSortedByProperty(map, "nonexistent" as any)).toThrow();
    });
  });

  // Test for converting map to object
  describe("convertMapToObject", () => {
    it("should correctly convert a Map to an Object", () => {
      const map: MapType<string, number> = new Map([
        ["a", 1],
        ["b", 2],
      ]);

      const result: ObjectType = MapUtil.convertMapToObject(map);
      const expected: ObjectType = {
        a: 1,
        b: 2,
      };
      expect(result).toEqual(result);
    });

    it("should handle undefined values correctly", () => {
      const map: MapType<string> = new Map([
        ["a", undefined],
        ["b", 2],
      ]);

      const result: ObjectType = MapUtil.convertMapToObject(map);
      const expected: ObjectType = {
        a: undefined,
        b: 2,
      };
      expect(result).toEqual(result);
    });
  });

  // Test for sorting a map based on its keys
  describe("sortMapByKey", () => {
    it("should correctly sort a Map by its keys", () => {
      const map: MapType = new Map([
        ["c", 1],
        ["a", 2],
        ["b", 3],
      ]);

      const result: MapType = MapUtil.sortMapByKey(map);
      const expected: MapType = new Map([
        ["a", 2],
        ["b", 3],
        ["c", 1],
      ]);
      expect(result).toEqual(result);
    });

    it("should handle undefined keys correctly", () => {
      const map: MapType = new Map([
        [undefined, 1],
        ["b", 2],
      ]);

      const result: MapType = MapUtil.sortMapByKey(map);
      const expected: MapType = new Map([
        [undefined, 1],
        ["b", 2],
      ]);

      expect(result).toEqual(result);
    });

    it("should handle complex keys correctly", () => {
      const map: MapType<{ value: number }, number> = new Map([
        [{ value: 3 }, 1],
        [{ value: 1 }, 2],
        [{ value: 2 }, 3],
      ]);

      const customCompareFunction = (a: { value: number }, b: { value: number }) => {
        return a.value - b.value;
      };

      const result: MapType = MapUtil.sortMapByKey(map, SortingMode.ASC, customCompareFunction);
      const expected: MapType = new Map([
        [{ value: 1 }, 2],
        [{ value: 2 }, 3],
        [{ value: 3 }, 1],
      ]);

      expect(result).toEqual(result);
    });
  });

  describe("hasPropertyKeysOnly", () => {
    it("returns true for maps with PropertyKey keys", () => {
      const map = new Map<string, number>([
        ["a", 1],
        ["b", 2],
      ]);
      expect(MapUtil.hasPropertyKeysOnly(map)).toBe(true);
    });

    it("returns false for maps with non-PropertyKey keys", () => {
      const map = new Map<object, string>([
        [{ x: 1 }, "a"],
        [{ y: 2 }, "b"],
      ]);
      expect(MapUtil.hasPropertyKeysOnly(map)).toBe(false);
    });

    it("returns false for maps with undefined keys", () => {
      const map = new Map<string | undefined, string>([
        [undefined, "a"],
        ["New", "b"],
      ]);
      expect(MapUtil.hasPropertyKeysOnly(map)).toBe(false);
    });

    it("returns false for non-map objects", () => {
      const object = { a: 1, b: 2 };
      expect(MapUtil.hasPropertyKeysOnly(object)).toBe(false);
    });
  });
});
