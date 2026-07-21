import {Collection} from "../javautil/Collection";

export interface IUMLPList<E> extends Collection<E> {

  add(indexOrElement: number | E, element?: E): Promise<boolean>;

  addFirst(element : E) : Promise<boolean>;

  addLast(element : E) : Promise<boolean>;

  addAll(idxOrElems: number | Collection<E> | Array<E>, elems?: Collection<E>): Promise<boolean>;

  get(index: number): Promise<E>;

  indexOf(o: any): Promise<number>;

  lastIndexOf(o: any): Promise<number>;

  // These two methods reprenset java.util.List#remove because if we have a List<Number> we cannot differentiate
  // between removing a specific element or removing an element at its index
  removeElement(element: E): Promise<boolean>;

  removeIndex(index: number): Promise<E>;

  set(index: number, element: E): Promise<E>;

  sort(param?: any): Promise<void>;

  subList(fromIndex: number, toIndex: number): Promise<IUMLPList<E>>;

  /**
   * This method must only be used in circumstances where you know that the IUMLPList is just a wrapper for a synchronous array.
   * It should NEVER be used in transpiled code.
   */
  __internalArray(): Array<E>;

}
