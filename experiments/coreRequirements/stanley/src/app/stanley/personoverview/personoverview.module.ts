// GEN-P

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { routes } from "./personoverview.routing";
import { PersonOverview } from "./person-overview/person-overview.component";
import { LayoutModule } from "../tmp/layout/layout.module";
import { TableModule } from "../tmp/table/table.module";

@NgModule({
  declarations: [
    PersonOverview
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    LayoutModule,
    TableModule
  ],
  exports: [
    PersonOverview
  ]
})
export class PersonOverviewModule { }
