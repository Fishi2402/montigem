import { Subscription } from "rxjs";
import { TypeCheckerUtil } from "../utils/type-checker.util";

/**
 * Utility class for managing Subscription objects, useful for preventing memory leaks with RxJS Observables. It
 * provides the following static functions:
 *
 * @example
 *   // Unsubscribes from a given Subscription.
 *   AutoUnsubscriber.PerformUnsubscription(subscription: Subscription)
 *   // Unsubscribes from all Subscriptions in an array.
 *   AutoUnsubscriber.UnsubscribeFromArray(subscriptions: Subscription[])
 *   // Unsubscribes from all Subscriptions in a class instance, with optional blacklist and subscriptionArrayName.
 *   AutoUnsubscriber.PerformAutoUnsubscription(instance: any, blacklist?: string[], subscriptionArrayName?: string)
 */
export class AutoUnsubscriber {
  /**
   * Unsubscribes from a given Subscription if possible.
   *
   * @param {Subscription} subscription - The Subscription to unsubscribe from (if unsubscribe is supported).
   */
  public static performUnsubscription(subscription: Subscription): void {
    if (subscription && TypeCheckerUtil.isFunction(subscription.unsubscribe)) subscription.unsubscribe();
  }

  /**
   * Unsubscribes from all Subscriptions in a given array.
   *
   * @param {Subscription[]} subscriptions - The array of Subscriptions to unsubscribe from.
   * @throws TypeError - If the provided value is not an array.
   */
  public static unsubscribeFromArray(subscriptions: Subscription[]): void {
    if (Array.isArray(subscriptions)) {
      subscriptions.forEach((element: Subscription): void => {
        AutoUnsubscriber.performUnsubscription(element);
      });
    } else {
      throw new TypeError(`Expected subscriptions to be an array, got ${typeof subscriptions} instead`);
    }
  }

  /**
   * Unsubscribes from all Subscriptions in a given class instance.
   *
   * @param {any} instance - The class instance.
   * @param {string[] | null} blacklist - An optional array of property names to ignore when searching for
   *   unsubscribable properties.
   * @param {string | null} subscriptionArrayName - The name of the property that holds an array of Subscriptions. If
   *   provided, only these Subscriptions will be unsubscribed from.
   */
  public static performAutoUnsubscription(
    instance: any,
    blacklist: string[] | null = null,
    subscriptionArrayName: string | null = null
  ): void {
    // If a subscription array name is provided, only unsubscribe from that array
    if (subscriptionArrayName) {
      AutoUnsubscriber.unsubscribeFromArray(instance[subscriptionArrayName]);
      return;
    }

    // Otherwise, unsubscribe from all properties that are not included in the blacklist
    for (const propertyName in instance) {
      if (blacklist?.includes(propertyName)) continue;
      AutoUnsubscriber.performUnsubscription(instance[propertyName]);
    }
  }
}

/**
 * Decorator to automatically unsubscribe from Subscriptions in a class when a lifecycle hook is called.
 *
 * This function was inspired by the implementation found in
 * [ngx-auto-unsubscribe](https://github.com/NetanelBasal/ngx-auto-unsubscribe/blob/01db5af64116b0e415388ef7079f8e53434e6ac6/auto-unsubscribe.ts)
 *
 * @param {string[] | null} blacklist - An optional array of property names to ignore when searching for unsubscribable
 *   properties.
 * @param {string | null} subscriptionArrayName - The name of the property that holds an array of Subscriptions. If
 *   provided, only these Subscriptions will be unsubscribed from.
 * @param {string} lifecycleHookName - The name of the lifecycle hook that will trigger the unsubscription. Defaults to
 *   "ngOnDestroy".
 * @returns {(constructor: any) => void} The decorated Class.
 */
export function AutoUnsubscribe(
  blacklist: string[] | null = null,
  subscriptionArrayName: string | null = null,
  lifecycleHookName: string = "ngOnDestroy"
): (constructor: any) => void {
  return function (constructor: any): void {
    // Get original function (if any)
    const originalLifecycleHook = constructor.prototype[lifecycleHookName];

    constructor.prototype[lifecycleHookName] = function (this, ...functionArguments: any[]): void {
      // Call the original function (if it exists)
      if (TypeCheckerUtil.isFunction(originalLifecycleHook)) {
        Reflect.apply(originalLifecycleHook, this, functionArguments);
      }

      // Unsubscribe from all Subscriptions
      AutoUnsubscriber.performAutoUnsubscription(this, blacklist, subscriptionArrayName);
    };
  };
}
