/* (c) https://github.com/MontiCore/monticore */

import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {GemTable} from "./gem-table.component";

@NgModule({
  declarations: [GemTable],
  imports: [CommonModule],
  exports: [GemTable]
})
export class GemtableModule {}
