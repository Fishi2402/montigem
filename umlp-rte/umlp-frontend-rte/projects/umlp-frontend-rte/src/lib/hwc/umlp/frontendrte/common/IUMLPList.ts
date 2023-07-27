export interface IUMLPList<E> extends AsyncIterable<E> {
  add(indexOrElement: number | E, element?: E): Promise<boolean>;
  addAll(index?: any, c?: any): Promise<boolean>;
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

  toArray(startIndex?: number, endIndex?: number): Promise<Array<E>>;

  // sort(c: java.util.Comparator<any>): any;
  [Symbol.asyncIterator](): AsyncIterator<E>;

  iterator(): AsyncIterator<E>;

  listIterator(from?: any): AsyncIterator<E>;

  join(delimiter: string): string;
}
