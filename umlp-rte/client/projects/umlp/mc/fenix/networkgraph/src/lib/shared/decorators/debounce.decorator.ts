import { DecoratorType, FunctionType } from "../utils/types";

/**
 * Debounces a given function which limits the rate at which a function can fire. The debounced function will delay its
 * invocation until after `timeout` milliseconds have elapsed since the last time it was invoked.
 *
 * @param {FunctionType} originalFunction - Function to debounce
 * @param {number} timeout - Debounce delay in milliseconds
 * @returns {FunctionType} - Debounced Function.
 */
export function applyDebounce(originalFunction: FunctionType, timeout: number): FunctionType {
  let timeoutReference: NodeJS.Timeout | null = null;

  // Clear the  timeout reference
  const clear = (): void => {
    if (timeoutReference) {
      clearTimeout(timeoutReference);
      timeoutReference = null;
    }
  };

  /**
   * Returns a new function that debounces the original function
   *
   * @param {...any} functionArguments - Function arguments
   */
  return function (this: any, ...functionArguments: any[]): void {
    clear();
    timeoutReference = setTimeout(() => {
      originalFunction.apply(this, functionArguments);
    }, timeout);
  };
}

/**
 * Debounce function decorator. Limits the rate at which a function can fire. The decorated function will delay its
 * invocation until after `timeout` milliseconds have elapsed since the last time it was invoked.
 *
 * @param {number} timeout - Debounce delay in milliseconds.
 * @returns {DecoratorType} - Debounced Function.
 */
export function Debounce(timeout: number): DecoratorType {
  return function (target: any, propertyKey: string, descriptor: PropertyDescriptor): PropertyDescriptor {
    // store original function for future use
    const original = descriptor.value;

    // Check if original is a function
    if (typeof original !== "function") {
      throw new TypeError(`Debounce decorator can only be applied to methods.`);
    }

    // override original function body with debounced function
    descriptor.value = applyDebounce(original, timeout);

    // return descriptor with new value
    return descriptor;
  };
}
