import {DeSer} from "@umlp-rte/target/command/DeSer";

export class SerializableDeSer implements DeSer<any> {
  deserialize(str: string): any {
    return undefined;
  }

  serialize(obj: any): string {
    return "";
  }

}
