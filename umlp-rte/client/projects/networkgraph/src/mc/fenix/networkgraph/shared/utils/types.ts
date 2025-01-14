/**
 * Type for a constructor. Any constructor takes an array of unknown arguments and returns an instance of a specified
 * type.
 *
 * @template T - The type that the constructor creates. Defaults to a non-nullable unknown type.
 * @type {new (...arguments_: any[]) => T}
 */
export type ConstructorType<T = NonNullable<unknown>> = new (...arguments_: any[]) => T;

/**
 * Type for event handlers. The event handler takes an event of type T and returns an unknown value.
 *
 * @template T - The type of event that the handler takes. Defaults to any type.
 * @type {(event: T) => unknown}
 */
export type EventHandlerType<T = any> = (event: T) => unknown;

/**
 * Type for any function. The function takes an array of any arguments and returns any value.
 *
 * @type {(...arguments_: any[]) => any}
 */
export type FunctionType = (...arguments_: any[]) => any;

/**
 * Type for any object. The object has string keys and unknown values.
 *
 * @template T - The type of the values in the object. Defaults to unknown.
 * @type {Record<PropertyKey, T>}
 */
export type ObjectType<T = unknown> = Record<PropertyKey, T>;

/**
 * Type for any array. The array has unknown values.
 *
 * @template T - The type of the values in the array. Defaults to unknown.
 * @type {T[]}
 */
export type ArrayType<T = unknown> = Array<T>;

/**
 * Type for any map. The map has unknown values by default.
 *
 * @template K - The type of the keys in the map. Defaults to unknown.
 * @template V - The type of the values in the map. Defaults to unknown.
 * @type {Map<K, V>}
 */
export type MapType<K = unknown, V = unknown> = Map<K, V>;

/**
 * Type makes property "K" required in Type "T" certain property required
 *
 * @template T - The type to make a property required in.
 * @template K - The name of the property to make required.
 * @type {T & { [P in K]-?: T[P] }}
 */
export type WithRequired<T, K extends keyof T> = T & { [P in K]-?: T[P] };

/**
 * Type for a nested array, for example [1, 2, 3, [4, [5, [6, [7]]]]] {@see https://stackoverflow.com/a/70313348}
 *
 * @template T - The type of the array items
 * @type {(NestedArray<T> | T)[]}
 */
export type NestedArray<T> = Array<NestedArray<T> | T>;

/**
 * Type for a comparator usable with ".sort()". It's a function that takes two arguments of the same type and returns a
 * number.
 *
 * @template T - The type of the arguments that the comparator function takes.
 * @type {(a: T, b: T) => number}
 */
export type ComparisonFunction<T> = (a: T, b: T) => number;

/**
 * Enum representing viable sorting modes.
 *
 * @readonly
 * @enum {string}
 */
export enum SortingMode {
  /** Ascending sorting mode. */
  "ASC" = "ASC",

  /** Descending sorting mode. */
  "DESC" = "DESC",
}

/**
 * A type alias for a function that takes a value of unknown type and returns a boolean. This is used for type checking
 * functions in the TypeChecker class.
 */
export type TypeCheckerFunction = (value: unknown) => boolean;

/**
 * TypeSelector that returns T2 if U extends "current", and T1 otherwise. This is used to extract "current" or
 * "previous" result from a difference.
 */
export type TypeSelector<T1, T2, U extends "current" | "previous"> = U extends "current" ? T2 : T1;

/**
 * Diff represents a simple difference between two types T1 and T2. Each type is optional, meaning it may or may not
 * exist.
 */
export type Diff<T1, T2> = {
  previous?: T1;
  current?: T2;
};

/**
 * MapDiff represents a complex difference between two map types. The map's key is a union of keys K1 and K2, and the
 * value is a Diff of V1 and V2.
 */
export type MapDiff<T1, T2> = [T1, T2] extends [Map<infer K1, infer V1>, Map<infer K2, infer V2>]
  ? Map<K1 | K2, Diff<V1, V2>>
  : never;

/**
 * ObjectDiff represents a difference between two object types. For each key K present in either T1 or T2, the value is
 * a Diff of T1[K] and T2[K].
 */
export type ObjectDiff<T1, T2> = { [K in keyof T1 | keyof T2]?: KeyDiff<T1, T2, K> };

/** KeyDiff represents a difference between two types T1 and T2. Each type is optional, meaning it */
type KeyDiff<T1, T2, K> = K extends keyof T1
  ? K extends keyof T2
    ? Diff<T1[K], T2[K]>
    : Diff<T1[K], undefined>
  : K extends keyof T2
    ? Diff<undefined, T2[K]>
    : never;

/**
 * Differences represent a generic difference between two types T1 and T2. Depending on the types of T1 and T2, it may
 * be a simple Diff, a MapDiff, or an ObjectDiff.
 *
 * - If T1 and T2 are `any` or `unknown`, the difference is a simple `Diff`.
 * - If T1 and T2 are both `maps`, the difference is a `MapDiff`.
 * - If T1 and T2 are both `iterables` (like arrays or sets), the difference is a simple `Diff`.
 * - If T1 and T2 are both `objects`, the difference is an `ObjectDiff`.
 * - In all other cases, the difference is a simple `Diff`.
 */
export type Differences<T1, T2> = [any, unknown] extends [T1, T2]
  ? Diff<T1, T2>
  : [T1, T2] extends [Map<unknown, unknown>, Map<unknown, unknown>]
    ? MapDiff<T1, T2>
    : [T1, T2] extends [Iterable<unknown>, Iterable<unknown>] // sets and arrays
      ? Diff<T1, T2>
      : [T1, T2] extends [{ [key: string]: unknown }, { [key: string]: unknown }]
        ? ObjectDiff<T1, T2>
        : Diff<T1, T2>;

/** Type for a decorator. */
export type DecoratorType = (target: any, propertyKey: string, descriptor: PropertyDescriptor) => PropertyDescriptor;

/**
 * Options to specify when deep merging data. By setting these flags to false, you can disable deep merging for certain
 * types. Then only the "new" data will be used rather than deep-merged with the old one. This constant sets the default
 * behavior for deep merging operations.
 */
export type DeepMergeOptions = {
  deepMergeObjects: boolean;
  deepMergeMaps: boolean;
  deepMergeArrays: boolean;
  deepMergeSets: boolean;
};
