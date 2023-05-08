// GEN-P: translated from Java

import { Component } from "@angular/core";
import { dummyPersons } from "../../../hardy/mock/person-dummy";
import { Person } from "../../../hardy/person";
import { TableColumn } from "../../tmp/table/table-types";

@Component({
  selector: "person-overview",
  templateUrl: "./person-overview.component.html",
  styleUrls: ["./person-overview.component.scss"]
})
export class PersonOverview {

  static readonly url: string = "personoverview/person-overview";
  static readonly title: string = "Person List Overview";

  public ps: Person[];

  protected _gemTableTColumns: TableColumn[] = [];

  constructor() {
    // TODO: person loading
    this.ps = dummyPersons;
  }

  public ngOnInit(): void {
    const gemTableTColumns: TableColumn[] = [];
    gemTableTColumns.push(new TableColumn("nickname"));
    this._gemTableTColumns = gemTableTColumns;
  }

  getInfoBoxBoxText() {
    return "This is some important information that you should know.";
  }

  getInfoBoxBoxColor() {
    return "#c7ddf2";
  }

  public getGemCardComp1Title(): string {
    return "Overview";
  }

  public getGemTableTRows(): Person[] {
    return this.ps;
  }

  public getGemTableTColumns(): TableColumn[] {
    return this._gemTableTColumns;
  }

  public deletePerson(p: Person): void {
    // deletion here
  }

}
