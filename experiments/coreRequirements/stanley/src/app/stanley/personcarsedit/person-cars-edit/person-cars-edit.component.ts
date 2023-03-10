// GEN-P: tranlated from Java

import { Component } from "@angular/core";
import { Car } from "../../../hardy/car";
import { dummy } from "../../../hardy/mock/person-dummy";
import { Person } from "../../../hardy/person";
import { TableColumn } from "../../tmp/table/table-types";

@Component({
  selector: "person-cars-edit",
  templateUrl: "./person-cars-edit.component.html",
  styleUrls: ["./person-cars-edit.component.scss"]
})
export class PersonCarsEdit {

  public p: Person;

  constructor() {
    // TODO: person loading
    this.p = dummy;
  }

  public ngOnInit(): void {
  }

  public getGemTextCarNumValue(): string {
    return this.p.getOwns().length.toString();
  }

  public getGemTextCarNumColor(): string {
    return "Blue";
  }

  public getGemTableTRows(): Car[] {
    return this.p.getOwns();
  }

  public getGemTableTColumns(): TableColumn[] {
    const res = [];
    return res;
  }

  public getGemTableT1Rows(): Car[] {
    return this.p.getChildren();
  }

  public getGemTableT1Columns(): TableColumn[] {
    const res = [new TableColumn("age", "Age"), new TableColumn("nickname", "Name")];
    return res;
  }

  public gemButtonNavLeftClick(): void {
    this.navigate();
  }

  public getGemButtonNavText(): string {
    return "Ok";
  }

  protected navigate(): void {
    // TODO: implementation missing
  }

}
