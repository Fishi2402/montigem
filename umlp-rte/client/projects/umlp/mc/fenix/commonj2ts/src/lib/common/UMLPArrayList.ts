import { NgIterable } from "@angular/core";
import {GemListBehaviorSubject, IUMLPList} from "../common/IUMLPList";
import {UMLPArrayListIterator} from "../common/UMLPArrayListIterator";
import {GemList} from "./GemList";
import {Gem} from "./Gem";

export class UMLPArrayList<E> implements IUMLPList<E> {

  subject: GemListBehaviorSubject<NgIterable<E>>;

  array: E[] = [];

  constructor(elemenets?: Array<E> | IUMLPList<E>) {
    if (!!elemenets) {
      if (elemenets instanceof Array) {
        this.array = elemenets;
      } else {
        this.array = elemenets.toArray();
        // elemenets.toArray().then(numbers => this.array = numbers);
      }
    }
  }

  public transformInternalArrays(): UMLPArrayList<any> {
    if (this.array.length == 0) {
      return this;
    }
    if (this.array[0] instanceof Array) {
      return new UMLPArrayList<any>(this.array.map((arr: E) => new UMLPArrayList(<Array<any>>arr)));
    }
    return this;
  }

  add(indexOrElement: number | E, elem?: E): Promise<boolean> {
    if (elem === undefined || elem === null) {
      this.array.push(<E>indexOrElement);
    } else {
      this.array.splice(<number>indexOrElement, 0, elem);
    }
    return Promise.resolve(true);
  }

  async addAll(idxOrElems: number | IUMLPList<E> | Array<E>, elems?: IUMLPList<E>): Promise<boolean> {
    if (elems === undefined || elems === null) {
      this.array.push(<E>idxOrElems);
    } else {
      this.array.splice(<number>idxOrElems, 0, ...(await <Array<E>>elems.toArray()));
    }
    return Promise.resolve(true);
  }

  clear(): void {
    this.array = [];
  }

  contains(o: any): Promise<boolean> {
    return Promise.resolve(this.array.includes(o));
  }

  async containsAll(o: IUMLPList<any> | Array<any>): Promise<boolean> {
    for await (const elem of o) {
      if (!this.array.includes(elem)) {
        return false;
      }
    }
    return true;
  }

  async forEach(consumer: (element: E) => void): Promise<void> {
    this.array.forEach(consumer);
  }

  get(index: number): Promise<E> {
    return Promise.resolve(this.array[index]);
  }

  indexOf(o: any): Promise<number> {
    return Promise.resolve(this.array.indexOf(o));
  }

  isEmpty(): boolean {
    return this.array.length === 0;
  }

  join(delimiter: string): string {
    return this.array.join(delimiter);
  }

  lastIndexOf(o: any): Promise<number> {
    return Promise.resolve(this.array.lastIndexOf(o));
  }

  map<T>(consumer: (element: E) => T): Promise<IUMLPList<T>> {
    return Promise.resolve(new UMLPArrayList(this.array.map(consumer)));
  }

  removeElement(element: E): Promise<boolean> {
    let index = this.array.indexOf(element);
    if (index > 0) {
      this.array.splice(index, 1);
      return Promise.resolve(true);
    } else {
      return Promise.resolve(false);
    }
  }

  removeIndex(index: number): Promise<E> {
    if (index >= 0 && index < this.array.length) {
      let element = this.array[index];
      this.array.splice(index, 1);
      return Promise.resolve(element);
    } else {
      return Promise.resolve(undefined);
    }
  }

  removeAll(c: IUMLPList<any>): Promise<boolean> {
    c.forEach(async e => {
      if (this.contains(c)) {
        const index = await this.indexOf(e);
        this.removeIndex(index);
      }
    })
    return Promise.resolve(true);
  }

  retainAll(c: IUMLPList<E>): boolean {
    return false; // TODO
  }

  set(index: number, element: E): Promise<E> {
    if (index >= 0 && index < this.array.length) {
      this.array[index] = element;
    } else {
      this.array.push(element);
    }
    return Promise.resolve(element);
  }

  size(): number {
    return this.array.length;
  }

  sort(param: any) {
    this.array.sort(param);
  }

  subList(fromIndex: number, toIndex: number): IUMLPList<E> {
    return undefined; // TODO
  }

  toArray(startIndex?: number, endIndex?: number): Array<E> {
    return this.array;
  }

  toArrayDeep(startIndex?: number, endIndex?: number): any {
    if (this.array.length == 0) {
      return this.toArray(startIndex, endIndex);
    } else if (this.array[0] instanceof GemList || this.array[0] instanceof UMLPArrayList) {
      return this.toArray(startIndex, endIndex).map(
        (v: E) => (v as unknown as IUMLPList<any>).toArrayDeep());
    } else {
      return this.toArray(startIndex, endIndex);
    }
  }


  [Symbol.asyncIterator](): AsyncIterator<E> {
    return new UMLPArrayListIterator(this.array);
  }

  iterator(): AsyncIterator<E> {
    return this[Symbol.asyncIterator]();
  }

  listIterator(from?: any): AsyncIterator<E> {
    return this[Symbol.asyncIterator]();
  }
}
