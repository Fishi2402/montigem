

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { routes } from "./personoverview.routing";
import { PersonOverview } from "./person-overview/person-overview.component";
import { LayoutModule } from "src/app/stanley/tmp/layout/layout.module";  
import { MyModule } from "../my/my.module";
import { TableModule } from "src/app/stanley/tmp/table/table.module";  

@NgModule({
  declarations: [
    PersonOverview
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LayoutModule, 
    MyModule,
    TableModule
  ],
  exports: [
    PersonOverview
  ]
})

export class PersonOverviewModule { }
