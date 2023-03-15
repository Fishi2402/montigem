

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { routes } from "./personcarsedit.routing";
import { PersonCarsEdit } from "./person-cars-edit/person-cars-edit.component";
import { IoModule } from "src/app/stanley/tmp/io/io.module";  
import { TableModule } from "src/app/stanley/tmp/table/table.module";  

@NgModule({
  declarations: [
    PersonCarsEdit
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    IoModule, 
    TableModule, 
  ],
  exports: [
    PersonCarsEdit
  ]
})

export class PersonCarsEditModule { }
