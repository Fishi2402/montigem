import {Log} from "../logging";

export class JsonPrinter {
  res = null;
  currElements = [];

  constructor(b: boolean) {

  }

  beginObject(kind?: string): void {
    let obj = {};
    if (this.currElements.length != 0 && !(this.currElements[0] instanceof Array)) {
      Log.error("0xA0600 JsonPrinter detected an invalid nesting of Json. "
        + "A JSON object within a JSON object must be a member!");
    }
    else if (!!kind) {
      if (this.currElements.length > 0) {
        (this.getCurrentElement())[kind] = obj;
      } else {
        Log.error("0xA0613 JsonPrinter detected an invalid nesting of Json. "
          + "Cannot add a member as the first element of a Json String!");
      }
      this.currElements.push(obj);
    } else {
      this.currElements.push(obj);
      if (this.res == null) {
        this.res = obj;
      }
    }
  }

  beginArray(kind?: string): void {
    let obj = [];
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

  endArray(): void {
    this.currElements.pop();
  }

  member(key: string, value: any): void {
    (this.getCurrentElement())[key] = value;
  }

  getContent(): any {
    return this.res;
  }

  memberJson(kind: string, value: string) {
    let parseElement: any = null;
    // TODO: Fallunterscheidung eleganter lösen
    try {
      parseElement = JSON.parse(value);
    } catch (err) {
      try {
        parseElement = JSON.parse("{\"" + kind + "\": \"" + value + "\"}")[kind];
      } catch (err) {
        Log.error("0xA0619 JsonPrinter cannot parse the provided JSON value: " + value);
      }
    }
    (this.getCurrentElement())[kind] = parseElement;
  }

  value(value: any) {
    // TODO
  }

  toString(): string {
    return JSON.stringify(this.res);
  }

  private getCurrentElement() {
    return this.currElements[this.currElements.length - 1];
  }
}
