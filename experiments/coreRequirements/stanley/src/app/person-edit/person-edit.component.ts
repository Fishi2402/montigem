import { Component } from "@angular/core";
import { PersonEditTOP } from "./person-edit-top.component";

@Component({
  selector: "person-edit",
  templateUrl: "./person-edit.component.html",
  styleUrls: ["./person-edit.component.scss", "./person-edit-top.component.scss"]
})
export class PersonEdit extends PersonEditTOP {

  constructor() {
    super();
  }

  // translated hwc

}
