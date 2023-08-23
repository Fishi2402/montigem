import {IUMLPList} from "../common/IUMLPList";
import {UMLPArrayListIterator} from "../common/UMLPArrayListIterator";
import {addWarning} from "@angular-devkit/build-angular/src/utils/webpack-diagnostics";

export class UMLPArrayList<E> implements IUMLPList<E> {


  array: E[] = [];

  constructor(elemenets?: Array<E> | IUMLPList<E>) {
    if (!!elemenets) {
      if (elemenets instanceof Array) {
        this.array = elemenets;
      } else {
        elemenets.toArray().then(numbers => this.array = numbers);
      }
    }
  }
  add(indexOrElement: number | E, elem?: E): Promise<boolean> {
    if (elem === undefined || elem === null) {
      this.array.push(<E>indexOrElement);
    } else {
      this.array.splice(<number>indexOrElement, 0, elem);
    }
    return Promise.resolve(true);
  }

  addAll(indexOrElements: number | Array<E>, elems?: Array<E>): Promise<boolean> {
    if (elems === undefined || elems === null) {
      this.array.push(<E>indexOrElements);
    } else {
      this.array.splice(<number>indexOrElements, 0, ...<Array<E>>elems);
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

  toArray (startIndex?:number, endIndex?:number): Promise<Array<E>> {
    return Promise.resolve(this.array);
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
