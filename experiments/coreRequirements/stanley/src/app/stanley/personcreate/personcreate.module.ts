

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { routes } from "./personcreate.routing";
import { PersonCreate } from "./person-create/person-create.component";
import { LayoutModule } from "src/app/stanley/tmp/layout/layout.module";  
import { IoModule } from "src/app/stanley/tmp/io/io.module";  

@NgModule({
  declarations: [
    PersonCreate
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LayoutModule, 
    IoModule, 
  ],
  exports: [
    PersonCreate
  ]
})

export class PersonCreateModule { }
