// GEN-P

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { GemRow } from "./gem-row/gem-row.component";
import { GemColumn } from "./gem-column/gem-column.component";
import { IoModule } from "../io/io.module";
import { GemCard } from "./gem-card/gem-card.component";

@NgModule({
  declarations: [
    GemRow,
    GemColumn,
    GemCard
  ],
  imports: [
    CommonModule,
    IoModule
  ],
  exports: [
    GemRow,
    GemColumn,
    GemCard
  ]
})
export class LayoutModule { }

