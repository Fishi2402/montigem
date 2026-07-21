import {AsyncStream} from "./AsyncStream";

export interface Collection<E> extends AsyncIterable<E> {

  hashCode(): Promise<number>;

  // TODO actually different from IUMLPList#add since List#add has an overload with index
  add(indexOrElement: number | E, element?: E): Promise<boolean>;

  // TODO actually different from IUMLPList#addAll since List#addAll has an overload with index
  addAll(idxOrElems: number | Collection<E> | Array<E>, elems?: Collection<E>): Promise<boolean>;

  clear(): Promise<void>;

  contains(o: any): Promise<boolean>;

  containsAll(c: Collection<any>): Promise<boolean>;

  equals(o: any): Promise<boolean>;

  isEmpty(): Promise<boolean>;

  // These two methods reprenset java.util.List#remove because if we have a List<Number> we cannot differentiate
  // between removing a specific element or removing an element at its index
  removeElement(element: E): Promise<boolean>;

  removeIndex(index: number): Promise<E>;

  removeAll(c: Collection<E>): Promise<boolean>;

  //TODO removeIf

  retainAll(c: Collection<E>): Promise<boolean>;

  size(): Promise<number>;

  // java.util.Iterator Interface
  iterator(): AsyncIterator<E>;

  forEach(consumer: (element: E) => void): Promise<void>;

  stream() : AsyncStream<E>;

  // TypeScript specifics
  toArrayDeepSync(startIndex?: number, endIndex?: number): Promise<Array<any>>;

  [Symbol.asyncIterator](): AsyncIterator<E>;

  listIterator(from?: any): AsyncIterator<E>;

  // TODO spliterator, stream, toArray
  spliterator() : never;
  parallelStream() : never;
  toArray<T>(x?:any) : never;

}
