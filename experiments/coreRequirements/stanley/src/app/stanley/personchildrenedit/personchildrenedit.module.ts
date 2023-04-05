import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { routes } from "./personchildrenedit.routing";
import { PersonChildrenEdit } from "./person-children-edit/person-children-edit.component";
import { InputModule } from "../tmp/io/input/input-top.module";

@NgModule({
  declarations: [
    PersonChildrenEdit
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    InputModule
  ],
  exports: [
    PersonChildrenEdit
  ]
})

export class PersonChildrenEditModule { }
