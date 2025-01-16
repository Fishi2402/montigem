import { DecoratorType, FunctionType } from "../utils/types";

/**
 * Throttles the execution of a given function, ensuring that it is only invoked once per specified duration.
 *
 * @param {FunctionType} originalFunction - Function to throttle.
 * @param {number} timeout - Throttle delay in milliseconds.
 * @param {boolean} trailing - Flag to indicate if the function should be executed at the end of the timeout.
 * @returns {FunctionType} - Throttled Function.
 */
export function applyThrottle(originalFunction: FunctionType, timeout: number, trailing: boolean = true): FunctionType {
  // WeakMaps to hold the last executed times and pending executions for each 'this' context
  const lastExecutedTimes: WeakMap<object, number> = new WeakMap();
  const pendingExecutions: WeakMap<object, NodeJS.Timeout> = new WeakMap();

  /**
   * Utility function returned by applyThrottle. It throttles the execution of the original
   *
   * @param {...any} arguments_ Function.
   */
  return function (this: any, ...arguments_: any[]): void {
    const currentTime: number = Date.now();
    const lastExecutedTime: number = lastExecutedTimes.get(this) || 0;
    const timeElapsed: number = currentTime - lastExecutedTime;

    // Function to execute the original function and update the last executed time
    const execute = (): void => {
      lastExecutedTimes.set(this, Date.now());
      originalFunction.apply(this, arguments_);
    };

    // If enough time has passed, execute the function
    if (timeElapsed >= timeout) {
      execute();

      // Clear any pending timeout for this context
      const pendingTimeout: NodeJS.Timeout | undefined = pendingExecutions.get(this);
      if (pendingTimeout) clearTimeout(pendingTimeout);
      pendingExecutions.delete(this);
    } else if (trailing) {
      // Clear any existing pending timeout for this context
      const pendingTimeout: NodeJS.Timeout | undefined = pendingExecutions.get(this);
      if (pendingTimeout) clearTimeout(pendingTimeout);

      // Schedule a new pending execution
      const nextTimeout: NodeJS.Timeout = setTimeout(() => {
        execute();
        pendingExecutions.delete(this);
      }, timeout - timeElapsed);

      pendingExecutions.set(this, nextTimeout);
    }
  };
}

/**
 * Decorator to throttle the execution of a method.
 *
 * @param {number} timeout - Throttle delay in milliseconds.
 * @param {boolean} trailing - Flag to indicate if the method should be executed at the end of the timeout.
 * @returns {DecoratorType} - Throttled Function.
 */
export function Throttle(timeout: number, trailing: boolean = false): DecoratorType {
  return function (target: any, propertyKey: string, descriptor: PropertyDescriptor): PropertyDescriptor {
    // store original function for future use
    const original = descriptor.value;

    // Check if the original is a function
    if (typeof original !== "function") {
      throw new TypeError(`Throttle decorator can only be applied to methods.`);
    }

    // Override the original function body with throttled function
    descriptor.value = applyThrottle(original, timeout, trailing);

    // return descriptor with new value
    return descriptor;
  };
}
