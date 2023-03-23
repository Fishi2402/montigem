import {Log} from "@umlp-rte/hwc/logging";

export class JsonPrinter {
  currElements = [];
  constructor(b: boolean) {

  }

  beginObject(kind?: string): void {
    let obj = {};
    if (!!kind) {
      if (this.currElements.length > 0) {
        (this.getCurrentElement())[kind] = obj;
      } else {
        Log.error("0xA0613 JsonPrinter detected an invalid nesting of Json. "
          + "Cannot add a member as the first element of a Json String!");
      }
    }
    this.currElements.push(obj);
  }

  endObject(): void {
    this.currElements.pop();
  }

  member(key: string, value: any): void {
    (this.getCurrentElement())[key] = value;
  }

  getContent(): any {
    return this.getCurrentElement();
  }

  private getCurrentElement() {
    return this.currElements[this.currElements.length - 1];
  }

  memberJson(value: string, s: string) {
    // TODO
  }
}
