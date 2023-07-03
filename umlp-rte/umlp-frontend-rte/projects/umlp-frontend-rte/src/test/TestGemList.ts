import {GemIdentifiable, GemList, LoadResult} from "../lib";

export class TestGem implements GemIdentifiable {
  getGemId(): number {
    return 0;
  }

}
export class TestGemList extends GemList<TestGem> {
  loadObjects(gemIDs: Array<number>): Promise<Array<LoadResult<TestGem>>> {
    return Promise.resolve(undefined);
  }

}
