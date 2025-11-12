import { TypeCheckerUtil, TypeEnum } from "./type-checker.util";
import { EventEmitter } from "@angular/core";

describe("TypeChecker", () => {
  describe("getType and is... methods", () => {
    it("String", () => {
      const testVariable = "test";
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.STRING);
      expect(TypeCheckerUtil.isString(testVariable)).toBe(true);
    });

    it("Number", () => {
      const testVariable = 123;
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.NUMBER);
      expect(TypeCheckerUtil.isNumber(testVariable)).toBe(true);
    });

    it("Boolean", () => {
      const testVariable = true;
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.BOOLEAN);
      expect(TypeCheckerUtil.isBoolean(testVariable)).toBe(true);
    });

    it("Undefined", () => {
      let testVariable;
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.UNDEFINED);
      expect(TypeCheckerUtil.isUndefined(testVariable)).toBe(true);
    });

    it("Null", () => {
      const testVariable = null;
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.NULL);
      expect(TypeCheckerUtil.isNull(testVariable)).toBe(true);
    });

    it("Symbol", () => {
      const testVariable = Symbol();
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.SYMBOL);
      expect(TypeCheckerUtil.isSymbol(testVariable)).toBe(true);
    });

    it("Object", () => {
      const testVariable = {};
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.OBJECT);
      expect(TypeCheckerUtil.isObject(testVariable)).toBe(true);
    });

    it("Array", () => {
      const testVariable: unknown[] = [];
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.ARRAY);
      expect(TypeCheckerUtil.isArray(testVariable)).toBe(true);
    });

    it("Map", () => {
      const testVariable: Map<unknown, unknown> = new Map([["key", "value"]]);
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.MAP);
      expect(TypeCheckerUtil.isMap(testVariable)).toBe(true);
    });

    it("Function", () => {
      const testVariable = () => {};
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.FUNCTION);
      expect(TypeCheckerUtil.isFunction(testVariable)).toBe(true);
    });

    it("Valid Dates", () => {
      const valid = [
        new Date(),
        "20/08/2023",
        "08/20/2023",
        "2023/08/20",
        "2023-08-20",
        "20-08-2023",
        "08-20-2023",
        "20230820",
        "20.08.2023",
        "2023.08.20",
        "20 08 2023",
        "08 20 2023",
        "2023-08-25T13:45:30",
        "2023-08-25T13:45:30Z",
        "2023-08-25T13:45:30.123Z",
        "2023-08-25T13:45:30+01:00",
        "2023-08-25T13:45:30-05:00",
      ];
      valid.forEach((date: any) => {
        expect(TypeCheckerUtil.getType(date)).toBe(TypeEnum.DATE);
        expect(TypeCheckerUtil.isDate(date)).toBe(true);
      });
    });

    it("Invalid Dates", () => {
      const invalid = [
        "08-2023", // Missing day
        "Aug 32, 2023", // Invalid day
        "2023.13.20", // Invalid month
        "2023.08.2023", // Repeated year
        "20/2023", // Missing month
        "20.08", // Missing year
        "20.2023.08", // Wrong order
        "22.08.20233", // Wrong year
        "22.08.1899", // Wrong year
        "33.08.2023", // Wrong day
        "22.13.2023", // Wrong month
        123,
        "test",
        "12345",
        {
          date: new Date(),
        },
        [new Date()],
        "2000-00-01",
        "2000-13-01",
        "00/00/0000",
        "2000/02",
        "1899-12-31",
        "2100-01-01",
        "32/01/2000",
        "2000/01/00",
        "2000 13 01",
        "2000, 01, 00",
        "19-13-31",
        "00/00/00",
        "31 Dec 1999",
        "08, 20, 2023",
        "20, Aug, 2023",
        "Aug 20, 2023",
        "August 20, 2023",
        "Sunday, Aug 20, 2023",
        "Sat, 01 Jan 2000 00:00:00 GMT",
        true,
        null,
        undefined,
        new Date("invalid date"),
        "Date: 01/01/2000",
        "01/01/2000 is the date",
        "String 1",
        "String 01/01/2022",
        "2023-08-25T134530",
        "2023-08-25T13:45:30 EST",
        "String 2023-08-25",
        "2023-08-25T25:45:30Z",
        "2023-08-25T13:45:30+01:60",
      ];

      invalid.forEach((date: any) => {
        expect(TypeCheckerUtil.getType(date)).not.toBe(TypeEnum.DATE);
        expect(TypeCheckerUtil.isDate(date)).toBe(false);
      });
    });

    it("RegExp", () => {
      const testVariable = /abc/;
      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.REG_EXP);
      expect(TypeCheckerUtil.isRegExp(testVariable)).toBe(true);
    });

    it("Class", () => {
      class TestClass {}

      expect(TypeCheckerUtil.getType(TestClass)).toBe(TypeEnum.CLASS);
      expect(TypeCheckerUtil.isClass(TestClass)).toBe(true);
    });

    it("Set", () => {
      const testVariable = new Set([1, 2, 3]);

      expect(TypeCheckerUtil.getType(testVariable)).toBe(TypeEnum.SET);
      expect(TypeCheckerUtil.isSet(testVariable)).toBe(true);
    });

    it("ClassInstance", () => {
      class TestClass {}

      const instance: TestClass = new TestClass();

      expect(TypeCheckerUtil.getType(instance)).toBe(TypeEnum.CLASS_INSTANCE);
      expect(TypeCheckerUtil.isClassInstance(instance)).toBe(true);
    });

    it("isNullOrUndefined", () => {
      let testVariable;
      expect(TypeCheckerUtil.isNullOrUndefined(testVariable)).toBe(true);
      testVariable = null;
      expect(TypeCheckerUtil.isNullOrUndefined(testVariable)).toBe(true);
      testVariable = "test";
      expect(TypeCheckerUtil.isNullOrUndefined(testVariable)).toBe(false);
    });
  });

  describe("IsObject Tests", () => {
    it("isObject", () => {
      const testVariable1 = {};
      expect(TypeCheckerUtil.isObject(testVariable1)).toBe(true);

      const testVariable2: any[] = [];
      expect(TypeCheckerUtil.isObject(testVariable2)).toBe(false);

      const testVariable3 = null;
      expect(TypeCheckerUtil.isObject(testVariable3)).toBe(false);

      const testVariable4 = undefined;
      expect(TypeCheckerUtil.isObject(testVariable4)).toBe(false);

      const testVariable5 = "string";
      expect(TypeCheckerUtil.isObject(testVariable5)).toBe(false);

      const testVariable6 = 123;
      expect(TypeCheckerUtil.isObject(testVariable6)).toBe(false);

      const testVariable7 = () => {};
      expect(TypeCheckerUtil.isObject(testVariable7)).toBe(false);

      const testVariable8 = new RegExp("");
      expect(TypeCheckerUtil.isObject(testVariable8)).toBe(false);

      const testVariable9 = new Date();
      expect(TypeCheckerUtil.isObject(testVariable9)).toBe(false);

      class TestClass {}

      const testVariable10 = new TestClass();
      expect(TypeCheckerUtil.isObject(testVariable10)).toBe(false);

      const testVariable11 = TestClass;
      expect(TypeCheckerUtil.isObject(testVariable11)).toBe(false);

      const testVariable12 = new Map();
      expect(TypeCheckerUtil.isObject(testVariable12)).toBe(false);

      const testVariable13 = new Set();
      expect(TypeCheckerUtil.isObject(testVariable13)).toBe(false);
    });
  });

  describe("Angular And Custom Types", () => {
    it("isEventEmitter", () => {
      const testVariable = new EventEmitter();
      expect(TypeCheckerUtil.isEventEmitter(testVariable)).toBe(true);
      const notEventEmitter = {};
      expect(TypeCheckerUtil.isEventEmitter(notEventEmitter)).toBe(false);
    });

    it("isNullish", () => {
      let testVariable: any;
      expect(TypeCheckerUtil.isNullish(testVariable)).toBe(true);
      testVariable = null;
      expect(TypeCheckerUtil.isNullish(testVariable)).toBe(true);
      testVariable = [];
      expect(TypeCheckerUtil.isNullish(testVariable)).toBe(true);
      testVariable = {};
      expect(TypeCheckerUtil.isNullish(testVariable)).toBe(true);
      testVariable = "";
      expect(TypeCheckerUtil.isNullish(testVariable)).toBe(true);
      testVariable = "test";
      expect(TypeCheckerUtil.isNullish(testVariable)).toBe(false);
    });

    it("arePrimitives", () => {
      const testVariables = [123, "test", true];
      expect(TypeCheckerUtil.areAllPrimitives(...testVariables)).toBe(true);
      const mixedVariables = [123, "test", {}];
      expect(TypeCheckerUtil.areAllPrimitives(...mixedVariables)).toBe(false);
    });

    it("isPrimitive", () => {
      const testVariable1 = 123;
      expect(TypeCheckerUtil.isPrimitive(testVariable1)).toBe(true);
      const testVariable2 = {};
      expect(TypeCheckerUtil.isPrimitive(testVariable2)).toBe(false);
    });

    it("isPrimitiveType", () => {
      expect(TypeCheckerUtil.isTypePrimitive(TypeEnum.STRING)).toBe(true);
      expect(TypeCheckerUtil.isTypePrimitive(TypeEnum.OBJECT)).toBe(false);
    });

    it("isPropertyKey", () => {
      const testVariable1 = "key";
      expect(TypeCheckerUtil.isPropertyKey(testVariable1)).toBe(true);
      const testVariable2 = {};
      expect(TypeCheckerUtil.isPropertyKey(testVariable2)).toBe(false);
    });
  });

  describe("isArrayOfPrimitives", () => {
    it("should return true for an array of primitives", () => {
      expect(TypeCheckerUtil.isArrayOfPrimitives([1, "a", true])).toBe(true);
    });

    it("should return false for an array containing non-primitives", () => {
      expect(TypeCheckerUtil.isArrayOfPrimitives([1, "a", true, {}])).toBe(false);
    });
    it("should return false for an array containing non-primitives", () => {
      expect(TypeCheckerUtil.isArrayOfPrimitives([1, "a", true, new Date()])).toBe(false);
    });

    it("should return false for an empty array", () => {
      expect(TypeCheckerUtil.isArrayOfPrimitives([])).toBe(true);
    });
  });

  describe("isObjectOfPrimitives", () => {
    it("should return true for an object of primitives", () => {
      expect(TypeCheckerUtil.isObjectOfPrimitives({ a: 1, b: "a", c: true })).toBe(true);
    });

    it("should return false for an object containing non-primitives", () => {
      expect(TypeCheckerUtil.isObjectOfPrimitives({ a: 1, b: "a", c: true, d: {} })).toBe(false);
    });

    it("should return false for an object containing non-primitives", () => {
      expect(TypeCheckerUtil.isObjectOfPrimitives({ a: 1, b: "a", c: true, d: new Date() })).toBe(false);
    });

    it("should return false for an empty object", () => {
      expect(TypeCheckerUtil.isObjectOfPrimitives({})).toBe(true);
    });
  });

  describe("allOfType", () => {
    it("should return true when all values match the specified type", () => {
      const result = TypeCheckerUtil.areAllOfType(TypeEnum.STRING, "a", "b", "c");
      expect(result).toBe(true);
    });

    it("should return false when any value does not match the specified type", () => {
      const result = TypeCheckerUtil.areAllOfType(TypeEnum.STRING, "a", 2, "c");
      expect(result).toBe(false);
    });

    it("should ignore nullish values", () => {
      const result = TypeCheckerUtil.areAllOfType(TypeEnum.STRING, "a", undefined, null, "c");
      expect(result).toBe(true);
    });

    it("should handle Objects", () => {
      const testVariables = [{}, {}, {}];
      expect(TypeCheckerUtil.areAllOfType(TypeEnum.OBJECT, ...testVariables)).toBe(true);
      const mixedVariables = [{}, 123, {}];
      expect(TypeCheckerUtil.areAllOfType(TypeEnum.OBJECT, ...mixedVariables)).toBe(false);
    });

    it("should handle Arrays", () => {
      const testVariables = [[], [], []];
      expect(TypeCheckerUtil.areAllOfType(TypeEnum.ARRAY, ...testVariables)).toBe(true);
      const mixedVariables = [[], 123, []];
      expect(TypeCheckerUtil.areAllOfType(TypeEnum.ARRAY, ...mixedVariables)).toBe(false);
    });
  });

  describe("isOfType", () => {
    it("should return true when the value matches the specified type", () => {
      const result = TypeCheckerUtil.isOfType(TypeEnum.NUMBER, 5);
      expect(result).toBe(true);
    });

    it("should return false when the value does not match the specified type", () => {
      const result = TypeCheckerUtil.isOfType(TypeEnum.NUMBER, "5");
      expect(result).toBe(false);
    });
  });

  describe("isExpectedType", () => {
    it("should return true when the actual type matches the expected type", () => {
      const result = TypeCheckerUtil.isExpectedType(TypeEnum.BOOLEAN, TypeEnum.BOOLEAN);
      expect(result).toBe(true);
    });

    it("should return false when the actual type does not match the expected type", () => {
      const result = TypeCheckerUtil.isExpectedType(TypeEnum.BOOLEAN, TypeEnum.STRING);
      expect(result).toBe(false);
    });
  });

  describe("areAllObjectValuesPrimitive", () => {
    it("should return true if all values are primitive", () => {
      const obj = {
        a: "hello",
        b: 123,
        c: true,
      };
      expect(TypeCheckerUtil.areAllObjectValuesPrimitive(obj)).toBe(true);
    });

    it("should return false if some values are not primitive", () => {
      const obj = {
        a: "hello",
        b: [123],
        c: true,
      };
      expect(TypeCheckerUtil.areAllObjectValuesPrimitive(obj)).toBe(false);
    });

    it("should return true if all object values are primitive", () => {
      const obj = {
        name: "John",
        age: 25,
        hasDrivingLicense: true,
      };
      expect(TypeCheckerUtil.areAllObjectValuesPrimitive(obj)).toBe(true);
    });

    it("should return false if any object value is not primitive", () => {
      const obj = {
        name: "John",
        parents: ["Jane", "Doe"],
        hasDrivingLicense: true,
      };
      expect(TypeCheckerUtil.areAllObjectValuesPrimitive(obj)).toBe(false);
    });
  });
});
