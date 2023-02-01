import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { routes } from "./editiere-person-edit.routing";
import { EditierePersonEdit } from "./editiere-person-edit.component";
import { IoModule } from "../tmp/io/io.module";

@NgModule({
  declarations: [
    EditierePersonEdit
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    IoModule
  ],
  exports: [
    EditierePersonEdit
  ]
})
export class EditierePersonEditModule { }
