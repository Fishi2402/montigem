// format-value.pipe.spec.ts
import { FormatValuePipe } from "./value-formatter.pipe";

describe("FormatValuePipe", () => {
  let pipe: FormatValuePipe;

  beforeEach(() => {
    pipe = new FormatValuePipe();
  });

  test("should format null or undefined values as N/A", () => {
    expect(pipe.transform(null)).toEqual("N/A");
    expect(pipe.transform(undefined)).toEqual("N/A");
  });

  test("should format number values correctly", () => {
    expect(pipe.transform(1000)).toEqual("1,000");
    expect(pipe.transform(1000.123)).toEqual("1,000.12");
  });

  test("should format arrays of primitive types correctly", () => {
    expect(pipe.transform([1, 2, 3])).toEqual("[1, 2, 3]");
    expect(pipe.transform([1000.123, "Sample"])).toEqual("[1,000.12, Sample]");
  });

  test("should format date values correctly", () => {
    const date = new Date(2023, 8, 29); // September 29, 2023
    expect(pipe.transform(date)).toEqual("9/29/2023");
  });

  test("should format boolean values as Yes or No", () => {
    expect(pipe.transform(true)).toEqual("Yes");
    expect(pipe.transform(false)).toEqual("No");
  });

  test("should format other values as string", () => {
    expect(pipe.transform("hello")).toEqual("hello");
    expect(pipe.transform({})).toEqual("[object Object]");
  });
});
