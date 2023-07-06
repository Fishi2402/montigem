import {TestGemList} from "./TestGemList";
export class GemListTest {
  async test(): Promise<void> {
    let g = new TestGemList([1, 2, 3]);
    for await (const e of g) {
      console.log(e.getGemId())
    }
    let ga = g as AsyncIterable<number>;
    console.log(ga[1]);
  }
}
