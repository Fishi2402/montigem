import { NodeKeyFormatterPipe } from "./node-key-formatter.pipe";

describe("NodeKeyFormatterPipePipe", () => {
  let pipe: NodeKeyFormatterPipe;

  beforeEach(() => {
    pipe = new NodeKeyFormatterPipe();
  });

  it("should create an instance", () => {
    expect(pipe).toBeTruthy();
  });

  it('should format a numerical key with "#" prefix', () => {
    expect(pipe.transform("123")).toBe("(#123)");
  });

  it("should wrap a non-numerical key in parentheses", () => {
    expect(pipe.transform("ABC")).toBe("(ABC)");
  });
});
