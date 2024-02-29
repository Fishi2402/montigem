import { SortingMode } from "./types";
import { compareValues } from "./comparison.util";

// Test for comparison of values directly
describe("compareValues", () => {
  it("should correctly compare numbers in ascending order", () => {
    expect(compareValues(3, 5)).toBeLessThan(0);
    expect(compareValues(5, 5)).toEqual(0);
    expect(compareValues(7, 5)).toBeGreaterThan(0);
  });

  it("should correctly compare numbers in descending order", () => {
    expect(compareValues(3, 5, SortingMode.DESC)).toBeGreaterThan(0);
    expect(compareValues(5, 5, SortingMode.DESC)).toEqual(0);
    expect(compareValues(7, 5, SortingMode.DESC)).toBeLessThan(0);
  });

  it("should correctly compare strings in ascending order", () => {
    expect(compareValues("abc", "def")).toBeLessThan(0);
    expect(compareValues("abc", "abc")).toEqual(0);
    expect(compareValues("def", "abc")).toBeGreaterThan(0);
  });

  it("should correctly compare strings in descending order", () => {
    expect(compareValues("abc", "def", SortingMode.DESC)).toBeGreaterThan(0);
    expect(compareValues("abc", "abc", SortingMode.DESC)).toEqual(0);
    expect(compareValues("def", "abc", SortingMode.DESC)).toBeLessThan(0);
  });

  it("should correctly compare two booleans", () => {
    expect(compareValues(true, false, SortingMode.ASC)).toBeGreaterThan(0);
    expect(compareValues(false, true, SortingMode.ASC)).toBeLessThan(0);
    expect(compareValues(true, true, SortingMode.ASC)).toBe(0);
  });

  it("should handle null and undefined correctly", () => {
    expect(compareValues(null, 5, SortingMode.ASC)).toBeLessThan(0);
    expect(compareValues(5, null, SortingMode.ASC)).toBeGreaterThan(0);
    expect(compareValues(null, null, SortingMode.ASC)).toBe(0);
  });

  it("should throw an error for incomparable types", () => {
    expect(() => compareValues({ obj1: "test" }, { obj2: "test" }, SortingMode.ASC)).toThrow();
  });
});
