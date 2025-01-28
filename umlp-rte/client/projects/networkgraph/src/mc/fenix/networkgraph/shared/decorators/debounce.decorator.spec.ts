import { applyDebounce, Debounce } from "./debounce.decorator";
import { jest } from "@jest/globals";

describe("Debounce", () => {
  const timeout: number = 100;

  describe("applyDebounce", () => {
    let mockFunction: jest.Mock;
    let debouncedFunction: any;

    beforeEach(() => {
      mockFunction = jest.fn();
      debouncedFunction = applyDebounce(mockFunction, timeout);
    });

    it("should not call the function immediately", () => {
      debouncedFunction();
      expect(mockFunction).not.toHaveBeenCalled();
    });

    it("should call the function after the timeout", (done) => {
      debouncedFunction();
      setTimeout(() => {
        expect(mockFunction).toHaveBeenCalled();
        done();
      }, timeout + 1);
    });
  });

  describe("Debounce Decorator", () => {
    class TestComponent {
      public testProperty: string = "";

      @Debounce(timeout)
      method(): void {
        this.testProperty = "method called";
      }
    }

    let component: TestComponent;

    beforeEach(() => {
      component = new TestComponent();
    });

    it("should not call the method immediately", () => {
      component.method();
      expect(component.testProperty).toEqual("");
    });

    it("should call the method after the timeout", (done) => {
      component.method();
      setTimeout(() => {
        expect(component.testProperty).toEqual("method called");
        done();
      }, timeout + 1);
    });
  });
});
