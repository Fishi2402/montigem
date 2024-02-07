import {GemList} from "../common/GemList";
import {GemIdentifiable} from "../common/GemIdentifiable";

export class GemListAsyncIterator<E extends GemIdentifiable> implements AsyncIterator<E> {
  private index: number;
  private done: boolean;

  constructor(private gemList: GemList<E>) {
    this.index = 0;
    this.done = false;
  }

  next(): Promise<IteratorResult<E>> {
    if (this.done) {
      return Promise.resolve({done: this.done, value: undefined});
    } else if (this.index === this.gemList.size()) {
      this.done = true;
      return Promise.resolve({done: this.done, value: undefined});
    }
    let value: Promise<E> = this.gemList.get(this.index);
    ++this.index;
    return new Promise<IteratorResult<E>>((resolve) => {
      value.then(val => {
        resolve({
          done: this.done,
          value: val
        });
      })
    });
  }
}
