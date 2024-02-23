import { JsonUtil } from "./json.util";

describe("Object-JSON conversion", () => {
  it("should correctly convert a simple object to JSON and back", () => {
    const obj = {
      name: "John",
      age: 30,
    };
    const jsonString = JsonUtil.convertToJson(obj);
    const reconstructedObj = JsonUtil.convertFromJson(jsonString);
    expect(reconstructedObj).toEqual(obj);
  });

  it("should correctly convert an array to JSON and back", () => {
    const arr = [1, 2, 3, 4, 5];
    const jsonString = JsonUtil.convertToJson(arr);
    const reconstructedArr = JsonUtil.convertFromJson(jsonString);
    expect(reconstructedArr).toEqual(arr);
  });

  it("should correctly convert a Map to JSON and back", () => {
    const map = new Map([
      ["key", "value"],
      ["anotherKey", "anotherValue"],
    ]);
    const jsonString = JsonUtil.convertToJson(map);
    const reconstructedMap = JsonUtil.convertFromJson(jsonString);
    expect(reconstructedMap).toEqual(map);
  });

  it("should correctly convert a Set to JSON and back", () => {
    const set = new Set(["item1", "item2", "item3"]);
    const jsonString = JsonUtil.convertToJson(set);
    const reconstructedSet = JsonUtil.convertFromJson(jsonString);
    expect(reconstructedSet).toEqual(set);
  });

  it("should correctly convert nested structures to JSON and back", () => {
    const obj = {
      arr: [1, 2, 3],
      map: new Map<string, string | Set<string>>([
        ["key1", "value1"],
        ["key2", new Set(["setVal1", "setVal2"])],
      ]),
    };
    const jsonString = JsonUtil.convertToJson(obj);
    const reconstructedObj = JsonUtil.convertFromJson(jsonString);
    expect(reconstructedObj).toEqual(obj);
  });

  it("should handle edge cases, such as empty structures", () => {
    const map = new Map();
    const set = new Set();
    const arr: any[] = [];
    const jsonStringMap = JsonUtil.convertToJson(map);
    const jsonStringSet = JsonUtil.convertToJson(set);
    const jsonStringArr = JsonUtil.convertToJson(arr);
    const reconstructedMap = JsonUtil.convertFromJson(jsonStringMap);
    const reconstructedSet = JsonUtil.convertFromJson(jsonStringSet);
    const reconstructedArr = JsonUtil.convertFromJson(jsonStringArr);
    expect(reconstructedMap).toEqual(map);
    expect(reconstructedSet).toEqual(set);
    expect(reconstructedArr).toEqual(arr);
  });

  it("should correctly convert complex nested structures to JSON and back", () => {
    const obj = {
      name: "John",
      details: {
        age: 30,
        siblings: [
          {
            name: "Jane",
            hobbies: new Set(["reading", "swimming"]),
            scores: new Map([
              ["math", 95],
              ["english", 88],
            ]),
          },
          {
            name: "Doe",
            hobbies: new Set(["cycling"]),
            scores: new Map([
              ["math", 78],
              ["english", 85],
            ]),
          },
        ],
      },
      favorites: {
        color: "blue",
        numbers: [7, 11, 3],
        parents: new Map([
          ["father", "Robert"],
          ["mother", "Lucy"],
        ]),
      },
    };

    const jsonString = JsonUtil.convertToJson(obj);
    const reconstructedObj = JsonUtil.convertFromJson(jsonString);
    expect(reconstructedObj).toEqual(obj);
  });

  it("should throw on objects with circular references", () => {
    const obj: any = {
      name: "John",
      age: 30,
    };
    obj.self = obj; // Circular reference

    expect(() => JsonUtil.convertToJson(obj)).toThrow();
  });
});
