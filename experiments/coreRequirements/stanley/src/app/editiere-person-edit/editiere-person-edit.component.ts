import { Component } from "@angular/core";
import { EditierePersonEditTOP } from "./editiere-person-edit-top.component";

@Component({
  selector: "editiere-person-edit",
  templateUrl: "./editiere-person-edit.component.html",
  styleUrls: ["./editiere-person-edit.component.scss", "./editiere-person-edit-top.component.scss"]
})
export class EditierePersonEdit extends EditierePersonEditTOP {

  constructor() {
    super();
  }

}
