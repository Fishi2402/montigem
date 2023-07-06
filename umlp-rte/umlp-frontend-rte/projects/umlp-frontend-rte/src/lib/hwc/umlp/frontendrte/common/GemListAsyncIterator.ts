import {GemList} from "@umlp-rte/hwc/common/GemList";
import {info} from "ng-packagr/lib/utils/log";
import {GemIdentifiable} from "@umlp-rte/target/common/GemIdentifiable";

export class GemListAsyncIterator<E extends GemIdentifiable> implements AsyncIterator<E>{
  private index: number;
  private done: boolean;
  constructor(private gemList: GemList<E>) {
    this.index = 0;
    this.done = false;
  }

  next(): Promise<IteratorResult<E>> {
    if (this.done) {
      return this.promiseWithValue(undefined);
    } else if (this.index === this.gemList.size()) {
      this.done = true;
      return this.promiseWithValue(undefined);
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

  private promiseWithValue(val: E | undefined) {
    return new Promise<IteratorResult<E>>((resolve) => {
      resolve({
        done: this.done,
        value: val
      });
    });
  }
}
