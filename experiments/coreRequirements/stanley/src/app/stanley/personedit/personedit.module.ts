

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { routes } from "./personedit.routing";
import { PersonEdit } from "./person-edit/person-edit.component";
import { LayoutModule } from "src/app/stanley/tmp/layout/layout.module";  
import { IoModule } from "src/app/stanley/tmp/io/io.module";  

@NgModule({
  declarations: [
    PersonEdit
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LayoutModule, 
    IoModule, 
  ],
  exports: [
    PersonEdit
  ]
})

export class PersonEditModule { }
