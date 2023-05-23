/* (c) https://github.com/MontiCore/monticore */

import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatButtonToggleModule } from "@angular/material/button-toggle";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatListModule } from "@angular/material/list";
import { MatMenuModule } from "@angular/material/menu";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatRadioModule } from "@angular/material/radio";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";

import { MatTableModule } from "@angular/material/table";
//import { InputFilterModule } from "@shared/components/guidsl/filter/input-filter.module";
//import { InputModule } from "@shared/components/guidsl/input/input.module";
//import { PipesModule } from "@shared/pipes";
import { NgxDatatableModule } from "@swimlane/ngx-datatable";
import { TextMaskModule } from "angular2-text-mask";
import { ContextMenuModule, ContextMenuService } from "@perfectmemory/ngx-contextmenu";
import { GemTableComponent } from "./gem-table.component";
import { FocusColumnDirective } from "./focus-column.directive";
import { EnforceInputValueDirective } from "./enforce-input-value.directive";
import { MatSelectModule } from "@angular/material/select";
import { GemContextMenuComponent } from "../gemcontextmenu/gem-context-menu.component";
import { MatSortModule } from "@angular/material/sort";

@NgModule({
  imports: [
    CommonModule,
    MatTableModule,
    MatIconModule,
    MatSortModule,
    MatCheckboxModule,
    MatPaginatorModule,
    MatInputModule,
    MatButtonModule,
    TextMaskModule,
    MatMenuModule,
    MatListModule,
    MatIconModule,
    //InputFilterModule,
    MatCheckboxModule,
    MatRadioModule,
    MatDialogModule,
    NgxDatatableModule,
    //InputModule,
    //PipesModule,
    MatButtonToggleModule,
    MatSlideToggleModule,
    FormsModule,
    MatSelectModule,
    ContextMenuModule
  ],
  declarations: [
    GemTableComponent,
    FocusColumnDirective,
    EnforceInputValueDirective,
    GemContextMenuComponent,
  ],
  providers: [ContextMenuService],

  exports: [GemTableComponent],
})
export class GemtableModule {}
