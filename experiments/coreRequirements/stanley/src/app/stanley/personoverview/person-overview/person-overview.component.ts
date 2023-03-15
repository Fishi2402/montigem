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

  public ps: Person[];

  constructor() {
    // TODO: person loading
    this.ps = dummyPersons;
  }

  public ngOnInit(): void {
  }

  public getGemTableComp1Rows(): Person[] { 
    return this.ps;
  }

  public getGemTableComp1Columns(): TableColumn[] {
    let result: TableColumn[] = [];
    return result;
  }

}
