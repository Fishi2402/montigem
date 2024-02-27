import { MergeUtil } from "./merge.util";
import { ArrayType, MapType, ObjectType } from "./types";

import { Utils } from "./utils";

describe("MergeUtils Functions", () => {
  describe("Deep Merge", () => {
    // Test merging of any objects
    describe("Merge Objects", () => {
      it("should handle empty objects", () => {
        const merged: ObjectType = MergeUtil.deepMerge({}, { a: {}, b: {} }, {});
        const expected: ObjectType = { a: {}, b: {} };
        expect(merged).toEqual(expected);
      });

      it("should handle single-property objects", () => {
        const merged: ObjectType = MergeUtil.deepMerge({ a: 1 }, { a: 2 }, { a: 3 });
        const expected: ObjectType = { a: 3 };
        expect(merged).toEqual(expected);
      });

      it("should handle nested mixed structures", () => {
        const object1: ObjectType = { a: { b: 1 }, c: [1, 2] };
        const object2: ObjectType = { a: { b: 2 }, c: [3, 4] };
        const merged: ObjectType = MergeUtil.deepMerge(object1, object2);
        const expected: ObjectType = {
          a: { b: 2 },
          c: [3, 4],
        };
        expect(merged).toEqual(expected);
      });
    });

    // Test deep merging of arrays
    describe("Merge Arrays", () => {
      it("should handle empty arrays", () => {
        const merged: ArrayType = MergeUtil.deepMerge([], [], []);
        const expected: ArrayType = [];
        expect(merged).toEqual(expected);
      });

      it("should handle single-element arrays", () => {
        const merged: ArrayType = MergeUtil.deepMerge([1], [2], [3]);
        const expected: ArrayType = [3];
        expect(merged).toEqual(expected);
      });

      it("should handle nested structures", () => {
        const array1: ArrayType = [[{ a: 1 }]];
        const array2: ArrayType = [[{ b: 2 }]];
        const merged: ArrayType = MergeUtil.deepMerge(array1, array2);
        const expected: ArrayType = [[{ a: 1, b: 2 }]];
        expect(merged).toEqual(expected);
      });
    });

    // Test the merging of sets
    describe("Merge Sets", () => {
      it("should merge sets", () => {
        const set1: Set<unknown> = new Set([1, 2, 3]);
        const set2: Set<unknown> = new Set([2, 3, 4]);
        const merged: Set<unknown> = MergeUtil.deepMerge(set1, set2);
        const expected: Set<unknown> = new Set([1, 2, 3, 4]);
        expect(merged).toEqual(expected);
      });

      it("should handle merging with an empty set", () => {
        const set1: Set<unknown> = new Set([1, 2, 3]);
        const set2: Set<unknown> = new Set();
        const merged: Set<unknown> = MergeUtil.deepMerge(set1, set2);
        const expected: Set<unknown> = new Set([1, 2, 3]);
        expect(merged).toEqual(expected);
      });

      it("should handle merging sets with complex objects", () => {
        const set1: Set<unknown> = new Set([{ a: 1 }, { b: 2 }]);
        const set2: Set<unknown> = new Set([{ b: 2 }, { c: 3 }]);
        const merged: Set<unknown> = MergeUtil.deepMerge(set1, set2);
        const expected: Set<unknown> = new Set([{ a: 1 }, { b: 2 }, { c: 3 }]);
        expect(merged).toEqual(expected);
      });

      it("should handle different types correctly", () => {
        const object1: Set<unknown> = new Set([1, 2, { a: "obj" }]);
        const object2: Set<unknown> = new Set([{ a: 1 }, { b: 2 }, "value"]);
        const merged: Set<unknown> = MergeUtil.deepMerge(object1, object2);
        const expected: Set<unknown> = new Set([1, 2, { a: "obj" }, { a: 1 }, { b: 2 }, "value"]);
        expect(merged).toEqual(expected);
      });
    });

    // Test the merging of maps
    describe("Merge Maps", () => {
      it("should merge maps", () => {
        const map1: MapType = new Map([
          ["a", [1]],
          ["b", [2]],
        ]);
        const map2: MapType = new Map([
          ["b", [3]],
          ["c", [4]],
        ]);
        const merged: MapType = MergeUtil.deepMerge(map1, map2);
        const result: MapType = new Map([
          ["a", [1]],
          ["b", [3]],
          ["c", [4]],
        ]);
        expect(Utils.isEqual(merged, result)).toBe(true);
      });

      it("should merge maps", () => {
        const map1: MapType = new Map([
          ["a", 1],
          ["b", 2],
        ]);
        const map2: MapType = new Map([
          ["b", 3],
          ["c", 4],
        ]);
        const merged: MapType = MergeUtil.deepMerge(map1, map2);
        const expected: MapType = new Map([
          ["a", 1],
          ["b", 3],
          ["c", 4],
        ]);

        expect(merged).toEqual(expected);
      });
      it("should handle merging with an empty map", () => {
        const map1: MapType = new Map([
          ["a", 1],
          ["b", 2],
        ]);
        const map2: MapType = new Map();
        const merged: MapType = MergeUtil.deepMerge(map1, map2);
        const expected: MapType = new Map([
          ["a", 1],
          ["b", 2],
        ]);
        expect(merged).toEqual(expected);
      });

      it("should handle merging maps with complex objects as values", () => {
        const map1: MapType = new Map([
          ["a", { x: 1 }],
          ["b", { y: 2 }],
        ]);
        const map2: MapType = new Map([
          ["b", { y: 3, z: 4 }],
          ["c", { w: 5 }],
        ]);
        const merged: MapType = MergeUtil.deepMerge(map1, map2);
        const expected: MapType = new Map([
          ["a", { x: 1 }],
          ["b", { y: 3, z: 4 }],
          ["c", { w: 5 }],
        ]);
        expect(merged).toEqual(expected);
      });

      it("should throw an error for mixed types", () => {
        const map: MapType = new Map([["a", 1]]);
        const object: ObjectType = { b: 2 };
        expect(MergeUtil.deepMerge(map, object)).toEqual({ b: 2 });
      });

      it("should handle merging with an empty map", () => {
        const map1: MapType = new Map([
          ["a", 1],
          ["b", 2],
        ]);
        const map2: MapType = new Map();
        const merged: MapType = MergeUtil.deepMerge(map1, map2);
        const expected: MapType = new Map([
          ["a", 1],
          ["b", 2],
        ]);
        expect(merged).toEqual(expected);
      });

      it("should handle merging maps with complex objects as values", () => {
        const map1: MapType = new Map([
          ["a", { x: 1 }],
          ["b", { y: 2 }],
        ]);
        const map2: MapType = new Map([
          ["b", { y: 3, z: 4 }],
          ["c", { w: 5 }],
        ]);
        const merged: MapType = MergeUtil.deepMerge(map1, map2);
        const expected: MapType = new Map([
          ["a", { x: 1 }],
          ["b", { y: 3, z: 4 }],
          ["c", { w: 5 }],
        ]);

        expect(merged).toEqual(expected);
      });

      it("should compare keys by value", () => {
        const map1: MapType = new Map([
          [{ x: 1 }, "value 1"],
          [{ y: 2 }, "value 2"],
        ]);
        const map2: MapType = new Map([
          [{ x: 1 }, "updated"],
          [{ z: 3 }, "value 2"],
        ]);
        const merged: MapType = MergeUtil.deepMerge(map1, map2);
        const expected: MapType = new Map([
          [{ x: 1 }, "updated"],
          [{ y: 2 }, "value 2"],
          [{ z: 3 }, "value 2"],
        ]);

        expect(Utils.isEqual(merged, expected)).toBe(true);
      });

      it("should handle maps with complex keys and values", () => {
        const object1: MapType = new Map([
          [{ a: "Prop1" }, "first value"],
          [{ b: "Prop2" }, "second value"],
        ]);
        const object2: MapType = new Map([
          [{ a: "Prop1" }, "updated first value"],
          [{ c: 123 }, "third value from second object"],
        ]);
        const merged: MapType = MergeUtil.deepMerge(object1, object2);
        const expected: MapType = new Map([
          [{ a: "Prop1" }, "updated first value"],
          [{ b: "Prop2" }, "second value"],
          [{ c: 123 }, "third value from second object"],
        ]);

        expect(Utils.isEqual(merged, expected)).toBe(true);
      });
    });

    // Test merging of any objects/arrays
    describe("Mixed Merge Case", () => {
      it("should handle primitive types", () => {
        expect(MergeUtil.deepMerge(1, 2, 3)).toEqual(3);
      });

      it("should merge complex nested object with maps and references by value correctly", () => {
        const object1 = {
          a: 1,
          b: {
            c: {
              d: new Map([
                [{ a: "Prop1" }, "first value"],
                [{ b: "Prop2" }, "second value"],
              ]),
              e: [1, 2, 3],
              f: {
                g: 1,
                h: "string",
                i: new Set([1, 2, { a: "obj" }]),
              },
            },
          },
        };
        const object2 = {
          b: {
            c: {
              d: new Map([
                [{ a: "Prop1" }, "updated first value"],
                [{ c: 123 }, "third value from second object"],
              ]),
              e: ["string 1", "string 2", "string 3"],
              f: {
                h: 123,
                i: new Set([{ a: 1 }, { b: 2 }, "value"]),
              },
            },
            j: {
              added: "value",
            },
          },
        };
        const merged = MergeUtil.deepMerge(object1, object2);
        const expected = {
          a: 1,
          b: {
            c: {
              d: new Map([
                [{ a: "Prop1" }, "updated first value"],
                [{ b: "Prop2" }, "second value"],
                [{ c: 123 }, "third value from second object"],
              ]),
              e: ["string 1", "string 2", "string 3"],
              f: {
                g: 1,
                h: 123,
                i: new Set([1, 2, { a: "obj" }, { a: 1 }, { b: 2 }, "value"]),
              },
            },
            j: {
              added: "value",
            },
          },
        };

        expect(Utils.isEqual(merged, expected)).toBe(true);
      });
    });
  });

  describe("Deep Merge with Options", () => {
    describe("Mixed Merge Case with ", () => {
      it("should handle primitive types", () => {
        expect(MergeUtil.deepMerge(1, 2, 3)).toEqual(3);
      });

      it("should merge complex nested object with maps and references by value correctly", () => {
        const object1 = {
          a: 1,
          b: {
            c: {
              d: new Map([
                [{ a: "Prop1" }, "first value"],
                [{ b: "Prop2" }, "second value"],
              ]),
              e: [1, 2, 3],
              f: {
                g: 1,
                h: "string",
                i: new Set([1, 2, { a: "obj" }]),
              },
            },
          },
        };
        const object2 = {
          b: {
            c: {
              d: new Map([
                [{ a: "Prop1" }, "updated first value"],
                [{ c: 123 }, "third value from second object"],
              ]),
              e: ["string 1", "string 2", "string 3"],
              f: {
                h: 123,
                i: new Set([{ a: 1 }, { b: 2 }, "value"]),
              },
            },
            j: {
              added: "value",
            },
          },
        };
        const merged = MergeUtil.deepMerge(object1, object2);
        const expected = {
          a: 1,
          b: {
            c: {
              d: new Map([
                [{ a: "Prop1" }, "updated first value"],
                [{ b: "Prop2" }, "second value"],
                [{ c: 123 }, "third value from second object"],
              ]),
              e: ["string 1", "string 2", "string 3"],
              f: {
                g: 1,
                h: 123,
                i: new Set([1, 2, { a: "obj" }, { a: 1 }, { b: 2 }, "value"]),
              },
            },
            j: {
              added: "value",
            },
          },
        };

        expect(Utils.isEqual(merged, expected)).toBe(true);
      });
    });
  });
});
