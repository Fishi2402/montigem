export class Stream<T> implements Iterable<T> {
  // The source is no longer an array, but an Iterable.
  private readonly iterable: Iterable<T>;

  constructor(iterable: Iterable<T>) {
    this.iterable = iterable;
  }

  // Makes the stream itself iterable (important for internal yield* calls)
  public [Symbol.iterator](): Iterator<T> {
    return this.iterable[Symbol.iterator]();
  }

  // --- STATIC SOURCES ---

  public static of<T>(array: T[]): Stream<T>;
  public static of<T>(...values: T[]): Stream<T>;
  public static of<T>(...args: any[]): Stream<T> {
    if (args.length === 1 && Array.isArray(args[0])) {
      return new Stream<T>(args[0]);
    }
    return new Stream<T>(args);
  }

  public static empty<T>(): Stream<T> {
    return new Stream<T>([]);
  }

  // --- INTERMEDIATE OPERATIONS---

  public map<R>(mapper: (value: T) => R): Stream<R> {
    const source = this.iterable;
    return new Stream<R>({
      *[Symbol.iterator]() {
        for (const item of source) {
          yield mapper(item);
        }
      }
    });
  }

  public filter(predicate: (value: T) => boolean): Stream<T> {
    const source = this.iterable;
    return new Stream<T>({
      *[Symbol.iterator]() {
        for (const item of source) {
          if (predicate(item)) {
            yield item;
          }
        }
      }
    });
  }

  public flatMap<R>(mapper: (value: T) => any): Stream<R> {
    const source = this.iterable;
    return new Stream<R>({
      *[Symbol.iterator]() {
        for (const item of source) {
          const mapped = mapper(item);
          // Stream and Array are Iterables, so we can use yield*
          if (mapped instanceof Stream) {
            yield* mapped;
          } else if (Array.isArray(mapped)) {
            yield* mapped;
          } else if (mapped !== null && mapped !== undefined) {
            yield mapped;
          }
        }
      }
    });
  }

  public distinct(): Stream<T> {
    const source = this.iterable;
    return new Stream<T>({
      *[Symbol.iterator]() {
        const seen = new Set<T>();
        for (const item of source) {
          if (!seen.has(item)) {
            seen.add(item);
            yield item;
          }
        }
      }
    });
  }

  public limit(maxSize: number): Stream<T> {
    const source = this.iterable;
    return new Stream<T>({
      *[Symbol.iterator]() {
        if (maxSize <= 0) return;
        let count = 0;
        for (const item of source) {
          yield item;
          if (++count >= maxSize) {
            break; // Short-circuiting!
          }
        }
      }
    });
  }

  public skip(n: number): Stream<T> {
    const source = this.iterable;
    return new Stream<T>({
      *[Symbol.iterator]() {
        let count = 0;
        for (const item of source) {
          if (count < n) {
            count++;
          } else {
            yield item;
          }
        }
      }
    });
  }

  public peek(action: (value: T) => void): Stream<T> {
    const source = this.iterable;
    return new Stream<T>({
      *[Symbol.iterator]() {
        for (const item of source) {
          action(item);
          yield item;
        }
      }
    });
  }

  public sorted(comparator?: (a: T, b: T) => number): Stream<T> {
    // Sorting must consume everything before it can proceed
    // Therefore, we first collect everything into an array, sort it, and wrap it in a new Stream
    const array = Array.from(this.iterable);
    array.sort(comparator);
    return new Stream<T>(array);
  }

  // --- TERMINAL OPERATIONS  ---

  public toList(): Array<T> {
    return Array.from(this.iterable);
  }

  public toArray(): Array<T> {
    return Array.from(this.iterable);
  }

  public forEach(action: (value: T) => void): void {
    for (const item of this.iterable) {
      action(item);
    }
  }

  public forEachOrdered(action: (value: T) => void): void {
    this.forEach(action);
  }

  public count(): number {
    let count = 0;
    for (const _ of this.iterable) {
      count++;
    }
    return count;
  }

  public anyMatch(predicate: (value: T) => boolean): boolean {
    for (const item of this.iterable) {
      if (predicate(item)) return true;
    }
    return false;
  }

  public allMatch(predicate: (value: T) => boolean): boolean {
    for (const item of this.iterable) {
      if (!predicate(item)) return false;
    }
    return true;
  }

  public noneMatch(predicate: (value: T) => boolean): boolean {
    for (const item of this.iterable) {
      if (predicate(item)) return false;
    }
    return true;
  }

  public findFirst(): T | null {
    for (const item of this.iterable) {
      return item; // Pulls the very first element and immediately aborts
    }
    return null;
  }

  public findAny(): T | null {
    return this.findFirst();
  }

  public reduce(accumulator: (acc: T, curr: T) => T): T | null;
  public reduce<U>(identity: U, accumulator: (acc: U, curr: T) => U): U;
  public reduce(arg1: any, arg2?: any): any {
    let iterator = this.iterable[Symbol.iterator]();
    let current = iterator.next();

    if (arg2 === undefined) {
      if (current.done) return null;
      let acc = current.value;
      current = iterator.next();
      while (!current.done) {
        acc = arg1(acc, current.value);
        current = iterator.next();
      }
      return acc;
    } else {
      let acc = arg1;
      while (!current.done) {
        acc = arg2(acc, current.value);
        current = iterator.next();
      }
      return acc;
    }
  }

  public collect<R, A>(collector: any): R {
    if (collector && collector.name === 'toList') {
      return Array.from(this.iterable) as unknown as R;
    }
    if (collector && collector.name === 'joining') {
      return Array.from(this.iterable).join(collector.delimiter || '') as unknown as R;
    }

    return Array.from(this.iterable) as unknown as R;
  }
}

// Collector class remains unchanged
export class Collectors {
  public static toList() {
    return { name: 'toList' };
  }

  public static joining(delimiter: string = '') {
    return { name: 'joining', delimiter: delimiter };
  }
}
