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

  public getCarNumValue(): string {
    return this.p.getOwns().length.toString();
  }

  public getTRows(): Car[] {
    return this.p.getOwns();
  }

  public getTColumns(): TableColumn[] {
    const res = [];
    return res;
  }

  public navLeftClick(): void {
    this.navigate();
  }

  protected navigate(): void {
    // TODO: implementation missing
  }

}
