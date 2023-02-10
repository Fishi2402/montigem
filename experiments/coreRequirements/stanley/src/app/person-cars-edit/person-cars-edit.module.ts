import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { routes } from "./person-cars-edit.routing";
import { PersonCarsEdit } from "./person-cars-edit.component";
// actual path is different, change later
import { IoModule } from "../tmp/io/io.module";
import { TableModule } from "../tmp/table/table.module";

@NgModule({
  declarations: [
    PersonCarsEdit
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    IoModule,
    TableModule
  ],
  exports: [
    PersonCarsEdit
  ]
})
export class PersonCarsEditModule { }
