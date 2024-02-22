import { ObjectKeysPipePipe } from "./object-keys.pipe";

describe("KeysPipe", () => {
  let pipe: ObjectKeysPipePipe;

  beforeEach(() => {
    pipe = new ObjectKeysPipePipe();
  });

  it("create an instance", () => {
    expect(pipe).toBeTruthy();
  });

  it("should return keys of an object", () => {
    const testObj = { a: 1, b: 2, c: 3 };
    const result = pipe.transform(testObj);
    expect(result).toEqual(["a", "b", "c"]);
  });

  it("should return an empty array for null", () => {
    const result = pipe.transform(null);
    expect(result).toEqual([]);
  });

  it("should return an empty array for undefined", () => {
    const result = pipe.transform(undefined);
    expect(result).toEqual([]);
  });

  it("should return an empty array for non-object", () => {
    const result = pipe.transform(123);
    expect(result).toEqual([]);
  });
});
