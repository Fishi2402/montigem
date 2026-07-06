import {AsyncStream, StringHelper} from "./";

export class Arrays {
  public static asList<T>(...a: T[]): T[] {
    return [...a];
  }

  public static copyOf<T>(original: T[], newLength: number): T[] {
    return original.slice(0, newLength);
  }

  public static copyOfRange<T>(original: T[], from: number, to: number): T[] {
    return original.slice(from, to);
  }

  public static fill<T>(a: T[], val: T): void {
    this.fillRange(a, 0, a.length, val);
  }

  public static fillRange<T>(a: T[], fromIndex: number, toIndex: number, val: T): void {
    for (let i = fromIndex; i < toIndex; i++) {
      a[i] = val;
    }
  }

  public static sort<T>(a: T[]): void {
    a.sort();
  }

  public static stream<T>(array: T[]): AsyncStream<T> {
    return AsyncStream.of(array)
  }

  public static streamRange<T>(array: T[], startInclusive: number, endExclusive: number): AsyncStream<T> {
    return this.stream(array.slice(startInclusive, endExclusive));
  }

  public static toString<T>(a: T[]): string {
    return `[${a.map((v: T) => StringHelper.valueOf(v)).join(', ')}]`
  }
}
