import { NgIterable } from "@angular/core";
import { BehaviorSubject, Subscriber } from "rxjs";

/**
 * Custom BehaviorSubject with support for lazy-loading
 */
export class GemListBehaviorSubject<E> extends BehaviorSubject<E> {
  // Callback
  protected onSub: () => void;

  constructor(value: E, onSub: () => void) {
    super(value);
    this.onSub = onSub;
  }

  // Be notified about subscription, so we can load on-demand
  protected _innerSubscribe(subscriber: Subscriber<any>): Subscriber<any> {
    this.onSub();
    // @ts-ignore
    return super["_innerSubscribe"](subscriber);
  }
}

export interface IUMLPList<E> extends AsyncIterable<E> {
  subject: GemListBehaviorSubject<NgIterable<E>>;

  add(indexOrElement: number | E, element?: E): Promise<boolean>;

  addAll(idxOrElems: number | IUMLPList<E> | Array<E>, elems?: IUMLPList<E>): Promise<boolean>;

  clear(): void;

  contains(o: any): Promise<boolean>;

  containsAll(c: IUMLPList<any> | Array<any>): Promise<boolean>;

  forEach(consumer: (element: E) => void): Promise<void>;

  get(index: number): Promise<E>;

  indexOf(o: any): Promise<number>;

  isEmpty(): boolean;

  lastIndexOf(o: any): Promise<number>;

  map<T>(consumer: (element: E) => T): Promise<IUMLPList<T>>;

  removeElement(element: E): Promise<boolean>;

  removeIndex(index: number): Promise<E>;

  removeAll(c: IUMLPList<number>): Promise<boolean>;

  retainAll(c: IUMLPList<E>): boolean;

  set(index: number, element: E): Promise<E>;

  size(): number;

  sort(param: any): void;

  subList(fromIndex: number, toIndex: number): IUMLPList<E>;

  toArray(startIndex?: number, endIndex?: number): Array<E>;

  // sort(c: java.util.Comparator<any>): any;
  [Symbol.asyncIterator](): AsyncIterator<E>;

  iterator(): AsyncIterator<E>;

  listIterator(from?: any): AsyncIterator<E>;

  join(delimiter: string): string;
}
