// GEN-P

// always imported
import { NgModule } from "@angular/core";
// always imported
import { CommonModule } from "@angular/common";
// imported if routing is created, see GEN-P file .routing.ts, togehter with routes
import { RouterModule } from "@angular/router";
import { routes } from "./personcarsedit.routing";
// import of the component or page generated from the GUI model
// generator may assume, that there is a component translated from Java
import { PersonCarsEdit } from "./person-cars-edit/person-cars-edit.component";
// import of library, actual path is normally different, but this example still
// obeys generation rules:
// If a component is imported and used in a model, the module where the component is declared is
// looked up and imported in this module.
// Example: GemTable.gui is used in PersonCarsEdit.gui
// GemTable.gui is in package stanley.tmp.table -> stanley/tmp/table.module.ts
// PersonCarsEdit.gui is in package stanley.personcarsedit -> stanley/personcarsedit.module.ts
// -> relatve path ../tmp/table/table.module
// import doesn't have to be excessive (but can be, there is no different):
// For example, if a component from io module and io.input module are both imported,
// only io module is needed, since it already contains io.input module
import { IoModule } from "../tmp/io/io.module";
import { TableModule } from "../tmp/table/table.module";

// module is created for each package containing a component (not only page)
@NgModule({
  // all components of a package
  declarations: [
    PersonCarsEdit
  ],
  imports: [
    // always
    CommonModule,
    // imported if routing is created
    RouterModule.forChild(routes),
    // explanation above
    IoModule,
    TableModule
  ],
  // all components of a package
  exports: [
    PersonCarsEdit
  ]
})
export class PersonCarsEditModule { }
