import { ObjectUtil } from "./object.util";
import { ArrayType, MapType, ObjectType, SortingMode } from "./types";

describe("ObjectUtils", () => {
  // Test for keys of object sorted by property of object
  describe("getObjectKeysSortedByProperty", () => {
    it("should correctly sort object keys based on property values", () => {
      const object: ObjectType<{ prop: number }> = {
        a: { prop: 3 },
        b: { prop: 1 },
        c: { prop: 2 },
      };

      const result: ArrayType = ObjectUtil.getObjectKeysSortedByProperty(object, "prop");
      const expected: ArrayType = ["b", "c", "a"];
      expect(result).toEqual(expected);
    });

    it("should handle sorting modes correctly", () => {
      const object: ObjectType<{ prop: number }> = {
        a: { prop: 3 },
        b: { prop: 1 },
        c: { prop: 2 },
      };

      const result: ArrayType = ObjectUtil.getObjectKeysSortedByProperty(object, "prop", SortingMode.DESC);
      const expected: ArrayType = ["a", "c", "b"];
      expect(result).toEqual(expected);
    });

    it("should remove undefined values correctly", () => {
      const object: ObjectType<{ prop: number | undefined }> = {
        a: { prop: 3 },
        b: { prop: undefined },
        c: { prop: 2 },
      };

      const expected = ["a", "c"];

      expect(ObjectUtil.getObjectKeysSortedByProperty(object, "prop", SortingMode.DESC)).toEqual(expected);
    });

    it("should handle complex objects correctly", () => {
      const object: ObjectType<{ prop: { value: number } }> = {
        a: { prop: { value: 3 } },
        b: { prop: { value: 1 } },
        c: { prop: { value: 2 } },
      };

      const customCompareFunction = (a: { value: number }, b: { value: number }) => a.value - b.value;

      const result: ArrayType = ObjectUtil.getObjectKeysSortedByProperty(
        object,
        "prop",
        SortingMode.ASC,
        customCompareFunction
      );
      const expected: ArrayType = ["b", "c", "a"];
      expect(result).toEqual(expected);
    });

    it("should throw an error if property not found", () => {
      const object: ObjectType<{ prop: number }> = {
        a: { prop: 3 },
        b: { prop: 1 },
        c: { prop: 2 },
      };

      expect(() => ObjectUtil.getObjectKeysSortedByProperty(object, "nonexistent" as any)).toThrow();
    });
  });

  // Test resolving of fields for an object
  describe("resolveFieldData", () => {
    it("should resolve simple properties", () => {
      const data: ObjectType = {
        prop1: "hello",
      };

      const result = ObjectUtil.resolveFieldData(data, "prop1");
      const expected = "hello";
      expect(result).toEqual(expected);
    });

    it("should resolve nested properties", () => {
      const data: ObjectType = {
        prop1: {
          prop2: "hello",
        },
      };

      const result = ObjectUtil.resolveFieldData(data, "prop1.prop2");
      const expected = "hello";
      expect(result).toEqual(expected);
    });

    it("should handle undefined data", () => {
      const result = ObjectUtil.resolveFieldData(undefined as any, "prop1");

      expect(result).toBeNull();
    });

    it("should handle undefined fields", () => {
      const data: ObjectType = {
        prop1: "hello",
      };

      const result = ObjectUtil.resolveFieldData(data, undefined as any);

      expect(result).toBeNull();
    });

    it("should handle function fields", () => {
      const data: ObjectType = {
        prop1: "hello",
      };

      const result = ObjectUtil.resolveFieldData(data, (data: any) => data.prop1.toUpperCase());

      const expected = "HELLO";
      expect(result).toEqual(expected);
    });

    it("should throw error if field does not exist", () => {
      const data: ObjectType = {
        prop1: "hello",
      };

      expect(() => ObjectUtil.resolveFieldData(data, "prop2")).toThrow();
    });

    it("should throw error if nested field does not exist", () => {
      const data: ObjectType = {
        prop1: {
          prop2: "hello",
        },
      };

      expect(() => ObjectUtil.resolveFieldData(data, "prop1.prop3")).toThrow();
    });

    it("should throw error if field is not a string or function", () => {
      const data: ObjectType = {
        prop1: "hello",
      };

      expect(() => ObjectUtil.resolveFieldData(data, { prop1: "prop1" } as any)).toThrow();
    });
  });

  // Test for converting object to map
  describe("convertObjectToMap", () => {
    it("should correctly convert an Object to a Map", () => {
      const object: ObjectType = {
        a: 1,
        b: 2,
      };

      const result: MapType = ObjectUtil.convertObjectToMap(object);
      const expected: MapType<string, number> = new Map([
        ["a", 1],
        ["b", 2],
      ]);
      expect(result).toEqual(expected);
    });

    it("should handle undefined values correctly", () => {
      const object: ObjectType = {
        a: undefined,
        b: 2,
      };

      const result: MapType = ObjectUtil.convertObjectToMap(object);
      const expected: MapType<string> = new Map([
        ["a", undefined],
        ["b", 2],
      ]);

      expect(result).toEqual(expected);
    });
  });
});
