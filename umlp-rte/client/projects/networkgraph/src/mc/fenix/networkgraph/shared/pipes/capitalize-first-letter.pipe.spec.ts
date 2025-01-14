import { CapitalizeFirstLetterPipe } from "./capitalize-first-letter.pipe";

describe("CapitalizeFirstLetterPipe", () => {
  let pipe: CapitalizeFirstLetterPipe;

  beforeEach(() => {
    pipe = new CapitalizeFirstLetterPipe();
  });

  it("create an instance", () => {
    expect(pipe).toBeTruthy();
  });

  it("should capitalize the first letter of each word", () => {
    expect(pipe.transform("hello world")).toBe("Hello World");
  });

  it("should handle a single word", () => {
    expect(pipe.transform("hello")).toBe("Hello");
  });

  it("should handle an empty string", () => {
    expect(pipe.transform("")).toBe("");
  });

  it("should handle a null value", () => {
    expect(pipe.transform(null as any)).toBe(null as any);
  });
});
