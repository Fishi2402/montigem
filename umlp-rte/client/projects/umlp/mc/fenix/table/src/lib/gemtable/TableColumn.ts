import { GemTableColumn } from "./gem-table.type";

export class TableColumn implements GemTableColumn {

  prop: string;
  name: string;

  constructor(prop: string, name: string) {
    this.prop = prop;
    this.name = name;
  }

}
