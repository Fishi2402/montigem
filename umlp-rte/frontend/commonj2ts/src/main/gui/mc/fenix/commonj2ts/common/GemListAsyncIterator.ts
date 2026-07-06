import {GemList} from "../common/GemList";
import {GemIdentifiable} from "../common/GemIdentifiable";

export class GemListAsyncIterator<E extends GemIdentifiable> implements AsyncIterator<E> {
  private index: number;
  private done: boolean;

  constructor(private gemList: GemList<E>) {
    this.index = 0;
    this.done = false;
  }

  async next(): Promise<IteratorResult<E>> {
    if (this.done) {
      return {done: true, value: undefined};
    }
    if (this.index === await this.gemList.size()) {
      this.done = true;
      return {done: true, value: undefined};
    }
    const value = await this.gemList.get(this.index);
    ++this.index;
    return {done: false, value};
  }
}
