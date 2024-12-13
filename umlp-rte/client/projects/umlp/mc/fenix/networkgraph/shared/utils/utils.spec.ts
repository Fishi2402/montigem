import { Utils } from "./utils";

describe("Test Utils Functions", () => {
  describe("Deep Copy", () => {
    it("should clone nested objects", () => {
      const object = { key: { innerKey: "value" } };
      const copy = Utils.deepCopy(object);
      expect(copy.key).toEqual(object.key);
      expect(copy.key).not.toBe(object.key);
    });
    it("should clone arrays", () => {
      const object = { key: ["value"] };
      const copy = Utils.deepCopy(object);
      expect(copy.key).toEqual(object.key);
      expect(copy.key).not.toBe(object.key);
    });
    it("should clone maps", () => {
      const map = new Map([
        ["key1", { a: 1 }],
        ["key2", { a: 2 }],
      ]);
      const copy = Utils.deepCopy(map);
      expect(copy.get("key1")).toEqual(map.get("key1"));
      expect(copy.get("key1")).not.toBe(map.get("key1"));
    });

    it("modifying deep copy should not affect original object", () => {
      const object = { key: { innerKey: "value" } };
      const copy = Utils.deepCopy(object);
      copy.key.innerKey = "newValue";
      expect(object.key.innerKey).toEqual("value");
    });

    it("modifying deep copy should not affect original top-level properties", () => {
      const object = { key: "value" };
      const copy = Utils.deepCopy(object);
      copy.key = "newValue";
      expect(object.key).toEqual("value");
    });

    it("should clone maps", () => {
      const map = new Map([
        ["key1", { a: 1 }],
        ["key2", { a: 2 }],
      ]);
      const copy = Utils.deepCopy(map);
      copy.set("key1", { a: 3 });
      expect(copy.get("key1")).not.toEqual(map.get("key1"));
      expect(map.get("key1")).toEqual({ a: 1 });
      expect(copy.get("key1")).toEqual({ a: 3 });
    });

    it("should deep copy complex structures with functions", () => {
      const placeholderFunction1 = () => true;
      const placeholderFunction2 = () => "Sample";
      const obj = {
        array: [1, 2, placeholderFunction1],
        obj: {
          string: "Asdf",
          sampleFunction: placeholderFunction1,
        },
        map: new Map([["key1", placeholderFunction2]]),
        set: new Set(["key1", "key2", placeholderFunction2]),
      };

      const copy = Utils.deepCopy(obj);
      expect((copy.array[2] as any)()).toEqual(placeholderFunction1());
      expect(obj.obj.sampleFunction()).toEqual(placeholderFunction1());
      expect((obj.map.get("key1") as any)()).toEqual(placeholderFunction2());
      expect(([...obj.set][2] as any)()).toEqual(placeholderFunction2());
    });
  });

  describe("Is Equal", () => {
    it("should return true when arrays are equal", () => {
      const array1 = ["value1", "value2"];
      const array2 = ["value1", "value2"];
      expect(Utils.isEqual(array1, array2)).toBe(true);
    });

    it("should return false when arrays are not equal", () => {
      const array1 = ["value1", "value2"];
      const array2 = ["value1", "value3"];
      expect(Utils.isEqual(array1, array2)).toBe(false);
    });

    it("should return true when maps are equal", () => {
      const map1 = new Map().set("key", "value");
      const map2 = new Map().set("key", "value");
      expect(Utils.isEqual(map1, map2)).toBe(true);
    });

    it("should return false when maps are not equal", () => {
      const map1 = new Map().set("key", "value");
      const map2 = new Map().set("key", "otherValue");
      expect(Utils.isEqual(map1, map2)).toBe(false);
    });

    it("should correctly handle complex nested objects (example 1)", () => {
      const object1 = {
        a: [
          { x: "y", m: { p: [1, 2, { l: "m" }], q: "r" } },
          "string",
          null,
          {
            nested: {
              again: ["x", "y", "z", { a: { b: { c: "d" } } }],
              more: { nesting: ["yes", "no", null, { done: "finally" }] },
            },
          },
        ],
        b: new Map<unknown, unknown>([
          ["key", "value"],
          [1, new Set([1, 2, 3, [1, 2, 3], { a: "b" }])],
          [{ a: "b" }, "object"],
        ]),
        c: new Set([1, 2, 3, "a", "b", "c", { x: "y" }, [1, 2, 3]]),
      };

      const object2 = Utils.deepCopy(object1); // Create a deep copy

      expect(Utils.isEqual(object1, object2)).toBe(true);
    });

    it("should correctly handle complex nested objects (example 2)", () => {
      const object1 = {
        name: "John",
        age: 30,
        details: {
          hobbies: ["reading", "coding", { sport: "basketball" }],
          address: {
            street: "123 Main St",
            city: "New York",
            coordinates: { lat: 40.7128, lon: 74.006 },
            previous: [
              {
                city: "Chicago",
                years: 5,
              },
              {
                city: "San Francisco",
                years: 2,
              },
            ],
          },
        },
      };
      const object2 = Utils.deepCopy(object1); // Create a deep copy

      expect(object1).toEqual(object2);
      expect(Utils.isEqual(object1, object2)).toBe(true);
    });
  });
});
