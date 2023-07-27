import {DeSer} from "../command/DeSer";

export class SerializableDeSer implements DeSer<any> {
  deserialize(str: string): Promise<any> {
    return Promise.resolve(undefined);
  }

  serialize(obj: any): Promise<string> {
    return Promise.resolve("");
  }

}
