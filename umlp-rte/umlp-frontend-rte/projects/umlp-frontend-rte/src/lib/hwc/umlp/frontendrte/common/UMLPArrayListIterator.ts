import {GemList} from "../common/GemList";

export class UMLPArrayListIterator<E> implements AsyncIterator<E> {
  private index: number;
  private done: boolean;
  constructor(private array: Array<E>) {
    this.index = 0;
    this.done = false;
  }

  next(): Promise<IteratorResult<E>> {
    if (this.done) {
      return Promise.resolve({done: this.done, value: undefined});
    } else if (this.index === this.array.length) {
      this.done = true;
      return Promise.resolve({done: this.done, value: undefined});
    }
    let value: E = this.array[this.index];
    ++this.index;
    return Promise.resolve({done: this.done, value: value});
  }
}
