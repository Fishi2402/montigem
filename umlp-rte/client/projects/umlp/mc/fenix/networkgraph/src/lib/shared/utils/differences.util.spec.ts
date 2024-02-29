import { extractCurrentFromDifferences, extractPreviousFromDifferences, getDifferences } from "./differences.util";
import { Differences, ObjectDiff } from "./types";

describe("test differences", () => {
  describe("Testing extractCurrentFromDifferences and extractPreviousFromDifferences functions", () => {
    it("should correctly extract current and previous for primitive types", () => {
      const differences = getDifferences(5, 6);
      expect(extractCurrentFromDifferences(differences)).toEqual(6);
      expect(extractPreviousFromDifferences(differences)).toEqual(5);
    });

    it("should correctly extract current and previous for array types", () => {
      const differences = getDifferences([1, 2, 3], [1, 2, 3, 4]);
      expect(extractCurrentFromDifferences(differences)).toEqual([1, 2, 3, 4]);
      expect(extractPreviousFromDifferences(differences)).toEqual([1, 2, 3]);
    });

    it("should correctly extract current and previous for object types", () => {
      const differences = getDifferences({ a: 1, b: 2 }, { a: 1, b: 3 });
      expect(extractCurrentFromDifferences(differences)).toEqual({ b: 3 });
      expect(extractPreviousFromDifferences(differences)).toEqual({ b: 2 });
    });

    it("should correctly extract current and previous for complex object types", () => {
      const differences = getDifferences({ a: 1, b: { c: 2, d: [3, 4] } }, { a: 1, b: { c: 3, d: [3, 4, 5] } });
      expect(extractCurrentFromDifferences(differences)).toEqual({ b: { c: 3, d: [3, 4, 5] } });
      expect(extractPreviousFromDifferences(differences)).toEqual({ b: { c: 2, d: [3, 4] } });
    });

    it("should correctly extract current and previous for complex nested object types", () => {
      const differences = getDifferences(
        { a: 1, b: { c: new Map([["key", "value"]]), d: [3, { e: new Set([1, 2, 3]) }] } },
        { a: 1, b: { c: new Map([["key", "newValue"]]), d: [3, { e: new Set([1, 2, 3, 4]) }] } }
      );
      expect(extractCurrentFromDifferences(differences)).toEqual({
        b: { c: new Map([["key", "newValue"]]), d: [3, { e: new Set([1, 2, 3, 4]) }] },
      });
      expect(extractPreviousFromDifferences(differences)).toEqual({
        b: { c: new Map([["key", "value"]]), d: [3, { e: new Set([1, 2, 3]) }] },
      });
    });

    it("should correctly extract current and previous for Map types", () => {
      const differences: Differences<Map<string, string>, Map<string, string>> = getDifferences(
        new Map([
          ["key1", "value1"],
          ["key2", "value2"],
        ]),
        new Map([
          ["key1", "value1"],
          ["key2", "newvalue2"],
        ])
      );
      expect(extractCurrentFromDifferences<Map<string, string>, Map<string, string>>(differences)).toEqual(
        new Map([["key2", "newvalue2"]])
      );
      expect(extractPreviousFromDifferences<Map<string, string>, Map<string, string>>(differences)).toEqual(
        new Map([["key2", "value2"]])
      );
    });

    it("should correctly extract current and previous for Set types", () => {
      const differences = getDifferences(new Set([1, 2, 3]), new Set([1, 2, 3, 4]));
      expect(extractCurrentFromDifferences(differences)).toEqual(new Set([1, 2, 3, 4]));
      expect(extractPreviousFromDifferences(differences)).toEqual(new Set([1, 2, 3]));
    });
  });

  // Test for the getDifferences function
  describe("getDifferences function", () => {
    describe("Primitive Types", () => {
      it("should correctly handle cases where only one argument is a function", () => {
        const function_ = () => {};
        const diff = getDifferences(function_, "test");
        const expected = { previous: function_, current: "test" };
        expect(diff).toEqual(expected);
      });

      it("should correctly handle cases where both arguments are functions", () => {
        const function1 = () => {};
        const function2 = () => {};
        const diff = getDifferences(function1, function2);
        const expected = {};
        expect(diff).toEqual(expected);
      });

      it("should detect differences between different primitive types", () => {
        const diff = getDifferences(1, "1");
        const expected = { previous: 1, current: "1" };
        expect(diff).toEqual(expected);
      });

      it("should detect differences between same primitive types with different values", () => {
        const diff = getDifferences(1, 2);
        const expected = { previous: 1, current: 2 };
        expect(diff).toEqual(expected);
      });

      it("should not detect differences between same primitive types with same values", () => {
        const diff = getDifferences("test", "test");
        const expected = {};
        expect(diff).toEqual(expected);
      });

      it("should handle null and undefined correctly", () => {
        const diff = getDifferences(null, undefined as unknown);
        const expected = {};
        expect(diff).toEqual(expected);
      });

      it("should handle boolean values correctly", () => {
        const diff = getDifferences(true, false);
        const expected = { previous: true, current: false };
        expect(diff).toEqual(expected);
      });

      it("should handle symbols correctly", () => {
        const symbol1 = Symbol("symbol1");
        const symbol2 = Symbol("symbol2");
        const diff = getDifferences(symbol1, symbol2);
        const expected: any = { previous: symbol1, current: symbol2 };
        expect(diff).toEqual(expected);
      });
    });
    describe("Sets", () => {
      it("should detect differences between sets with different values", () => {
        const diff = getDifferences(new Set([1, 2, 3]), new Set([4, 5, 6]));
        const expected = { previous: new Set([1, 2, 3]), current: new Set([4, 5, 6]) };
        expect(diff).toEqual(expected);
      });

      it("should not detect differences between sets with the same values", () => {
        const diff = getDifferences(new Set([1, 2, 3]), new Set([1, 2, 3]));
        const expected = {};
        expect(diff).toEqual(expected);
      });

      it("should detect differences between sets with different number of elements", () => {
        const diff = getDifferences(new Set([1, 2]), new Set([1, 2, 3]));
        const expected = { previous: new Set([1, 2]), current: new Set([1, 2, 3]) };
        expect(diff).toEqual(expected);
      });

      it("should detect differences between sets with the same values in different order", () => {
        const diff = getDifferences(new Set([1, 2, 3]), new Set([3, 2, 1]));
        const expected = { previous: new Set([1, 2, 3]), current: new Set([3, 2, 1]) };
        expect(diff).toEqual(expected);
      });

      it("should detect differences when one set is a subset of the other", () => {
        const diff = getDifferences(new Set([1, 2]), new Set([1, 2, 3, 4]));
        const expected = { previous: new Set([1, 2]), current: new Set([1, 2, 3, 4]) };
        expect(diff).toEqual(expected);
      });

      it("should detect differences when one set is empty", () => {
        const diff = getDifferences(new Set(), new Set([1, 2, 3]));
        const expected = { previous: new Set(), current: new Set([1, 2, 3]) };
        expect(diff).toEqual(expected);
      });
    });
    describe("Arrays", () => {
      it("should correctly handle cases where both arguments are identical but not primitive", () => {
        const array = [1, 2, 3];
        const diff1 = getDifferences(array, array);
        const expected1 = {};
        expect(diff1).toEqual(expected1);

        const object = { a: 1, b: 2 };
        const diff2 = getDifferences(object, object);
        const expected2 = {};
        expect(diff2).toEqual(expected2);

        const map = new Map([["key", "value"]]);
        const diff3 = getDifferences(map, map);
        const expected3 = new Map();
        expect(diff3).toEqual(expected3);
      });

      it("should correctly handle arrays of different lengths", () => {
        const array1 = [1, 2, 3, 4, 5];
        const array2 = [1, 2, 3];
        const diff = getDifferences(array1, array2);
        const expected = { previous: array1, current: array2 };
        expect(diff).toEqual(expected);
      });

      it("should detect differences between arrays with different values", () => {
        const diff = getDifferences([1, 2, 3], [4, 5, 6]);
        const expected = { previous: [1, 2, 3], current: [4, 5, 6] };
        expect(diff).toEqual(expected);
      });

      it("should not detect differences between arrays with the same values", () => {
        const diff = getDifferences([1, 2, 3], [1, 2, 3]);
        const expected = {};
        expect(diff).toEqual(expected);
      });

      it("should detect differences between arrays with different object values", () => {
        const diff = getDifferences([{ obj: 1 }], [{ obj: 2 }]);
        const expected = { previous: [{ obj: 1 }], current: [{ obj: 2 }] };
        expect(diff).toEqual(expected);
      });

      it("should return an empty object when the two arrays are identical", () => {
        const diff1 = getDifferences([1, 2, 3], [1, 2, 3]);
        const expected1 = {};
        expect(diff1).toEqual(expected1);

        const diff2 = getDifferences(["hello", "world"], ["hello", "world"]);
        const expected2 = {};
        expect(diff1).toEqual(expected1);
      });

      it("should handle arrays of objects correctly", () => {
        const object1 = [{ a: 1 }];
        const object2 = [{ a: 2 }];
        const diff = getDifferences(object1, object2);
        const expected = { previous: object1, current: object2 };
        expect(diff).toEqual(expected);
      });

      it("should return an object with previous and current when the two arrays are different", () => {
        const diff1 = getDifferences([1, 2, 3], [4, 5, 6]);
        const expected1 = { previous: [1, 2, 3], current: [4, 5, 6] };
        expect(diff1).toEqual(expected1);

        const diff2 = getDifferences(["hello", "world"], ["foo", "bar"]);
        const expected2 = { previous: ["hello", "world"], current: ["foo", "bar"] };
        expect(diff2).toEqual(expected2);
      });

      it("should return differences when one of the arrays is empty", () => {
        const diff = getDifferences([], [1, 2, 3]);
        const expected = { previous: [], current: [1, 2, 3] };
        expect(diff).toEqual(expected);
      });

      it("should return differences when the arrays have different lengths", () => {
        const diff = getDifferences([1, 2], [1, 2, 3]);
        const expected = { previous: [1, 2], current: [1, 2, 3] };
        expect(diff).toEqual(expected);
      });
    });

    describe("Maps", () => {
      it("should return an empty object when the two arguments are identical", () => {
        const diff1 = getDifferences(
          new Map([
            ["key1", "value1"],
            ["key2", "value2"],
          ]),
          new Map([
            ["key1", "value1"],
            ["key2", "value2"],
          ])
        );
        const expected1 = new Map();
        expect(diff1).toEqual(expected1);
      });

      it("should detect differences between maps with different keys", () => {
        const diff = getDifferences(new Map([["key1", "value1"]]), new Map([["key2", "value2"]]));
        const expected = new Map([
          ["key1", { previous: "value1", current: undefined }],
          ["key2", { previous: undefined, current: "value2" }],
        ]);
        expect(diff).toEqual(expected);
      });

      it("should detect differences between maps with same keys but different values", () => {
        const diff = getDifferences(new Map([["key1", "value1"]]), new Map([["key1", "value2"]]));
        const expected = new Map([["key1", { previous: "value1", current: "value2" }]]);
        expect(diff).toEqual(expected);
      });

      it("should not detect differences between maps with same keys and values", () => {
        const diff = getDifferences(new Map([["key1", "value1"]]), new Map([["key1", "value1"]]));
        const expected = new Map();
        expect(diff).toEqual(expected);
      });

      it("should correctly handle dates as keys in maps", () => {
        const dateKey1 = new Date(2023, 0, 1);
        const dateKey2 = new Date(2023, 0, 2);
        const diff = getDifferences(new Map([[dateKey1, "value1"]]), new Map([[dateKey2, "value2"]]));
        const expected = new Map([
          [dateKey1, { previous: "value1", current: undefined }],
          [dateKey2, { previous: undefined, current: "value2" }],
        ]);
        expect(diff).toEqual(expected);
      });

      it("should correctly handle objects as keys in maps", () => {
        const objectKey1 = { key: 1 };
        const objectKey2 = { key: 2 };
        const diff = getDifferences(new Map([[objectKey1, "value1"]]), new Map([[objectKey2, "value2"]]));
        const expected = new Map([
          [objectKey1, { previous: "value1", current: undefined }],
          [objectKey2, { previous: undefined, current: "value2" }],
        ]);
        expect(diff).toEqual(expected);
      });

      it("should return a Map with differences when the two maps are different", () => {
        const diff = getDifferences(
          new Map([
            ["key1", "value1"],
            ["key2", "value2"],
          ]),
          new Map([
            ["key1", "value3"],
            ["key2", "value4"],
          ])
        );
        const expected = new Map([
          ["key1", { previous: "value1", current: "value3" }],
          ["key2", { previous: "value2", current: "value4" }],
        ]);
        expect(diff).toEqual(expected);
      });

      it("should return differences when one of the maps is empty", () => {
        const diff = getDifferences(
          new Map(),
          new Map([
            ["key1", "value1"],
            ["key2", "value2"],
          ])
        );
        const expected = new Map([
          ["key1", { previous: undefined, current: "value1" }],
          ["key2", { previous: undefined, current: "value2" }],
        ]);
        expect(diff).toEqual(expected);
      });

      it("should handle maps with non-string keys correctly", () => {
        const key1 = { a: 1 };
        const key2 = { a: 2 };
        const diff = getDifferences(new Map([[key1, "value1"]]), new Map([[key2, "value1"]]));
        const expected = new Map([
          [key1, { previous: "value1", current: undefined }],
          [key2, { previous: undefined, current: "value1" }],
        ]);
        expect(diff).toEqual(expected);
      });
    });

    describe("Objects", () => {
      it("should detect differences between objects with different keys", () => {
        const diff = getDifferences({ key1: "value1" }, { key2: "value2" });
        const expected = {
          key1: { previous: "value1", current: undefined },
          key2: { previous: undefined, current: "value2" },
        };
        expect(diff).toEqual(expected);
      });

      it("should detect differences between objects with same keys but different values", () => {
        const diff = getDifferences({ key1: "value1" }, { key1: "value2" });
        const expected = { key1: { previous: "value1", current: "value2" } };
        expect(diff).toEqual(expected);
      });

      it("should detect differences between objects with nested different values", () => {
        const diff = getDifferences(
          { key1: { nestedProp: { val: "test1" } } },
          { key1: { nestedProp: { val: "test2" } } }
        );
        const expected = {
          key1: {
            previous: { nestedProp: { val: "test1" } },
            current: { nestedProp: { val: "test2" } },
          },
        };
        expect(diff).toEqual(expected);
      });

      it("should not detect differences between objects with same keys and values", () => {
        const diff = getDifferences({ key1: "value1" }, { key1: "value1" });
        const expected = {};
        expect(diff).toEqual(expected);
      });

      it("should return an object with differences when the two objects are different", () => {
        const diff1 = getDifferences({ a: 1, b: 2 }, { a: 3, b: 4 });
        const expected1 = { a: { previous: 1, current: 3 }, b: { previous: 2, current: 4 } };
        expect(diff1).toEqual(expected1);

        const diff2 = getDifferences({ hello: "world", foo: "bar" }, { hello: "foo", foo: "world" });
        const expected2 = {
          hello: { previous: "world", current: "foo" },
          foo: { previous: "bar", current: "world" },
        };
        expect(diff2).toEqual(expected2);
      });

      it("should return differences when one of the objects is empty", () => {
        const diff = getDifferences({}, { a: 1, b: 2 });
        const expected = {
          a: { previous: undefined, current: 1 },
          b: { previous: undefined, current: 2 },
        };
        expect(diff).toEqual(expected);
      });

      it("should handle nested objects correctly", () => {
        const object1 = { a: { b: 1 } };
        const object2 = { a: { b: 2 } };
        const diff = getDifferences(object1, object2);
        const expected = { a: { previous: { b: 1 }, current: { b: 2 } } };
        expect(diff).toEqual(expected);
      });

      it("should handle removed keys correctly", () => {
        const object1 = { a: 1, b: 2 };
        const object2 = { a: 1 };
        const diff = getDifferences(object1, object2);
        const expected: ObjectDiff<{ a: number; b: number }, { a: number }> = {
          b: { previous: 2, current: undefined },
        };
        expect(diff).toEqual(expected);
      });

      it("should correctly handle deeply nested objects", () => {
        const object1 = { a: { b: { c: { d: { e: "value1" } } } } };
        const object2 = { a: { b: { c: { d: { e: "value2" } } } } };
        const diff = getDifferences(object1, object2);
        const expected = { a: { previous: object1.a, current: object2.a } };
        expect(diff).toEqual(expected);
      });

      it("should correctly find differences in nested objects by value", () => {
        const object1 = {
          name: "John",
          age: 30,
          address: {
            street: "123 Main St",
            city: "New York",
            nesting: { changedProperty: "Old", removedProperty: "Removed" },
          },
        };
        const object2 = {
          name: "John",
          age: 31,
          address: { street: "123 Main St", city: "New York", nesting: { changedProperty: "New" } },
        };
        const diff = getDifferences(object1, object2);
        const expected = {
          age: { previous: 30, current: 31 },
          address: {
            previous: {
              street: "123 Main St",
              city: "New York",
              nesting: { changedProperty: "Old", removedProperty: "Removed" },
            },
            current: {
              street: "123 Main St",
              city: "New York",
              nesting: { changedProperty: "New" },
            },
          },
        };
        expect(diff).toEqual(expected);
      });
    });
  });
});
