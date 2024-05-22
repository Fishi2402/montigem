import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { GemGrid } from './gem-grid';
import { GemgridcellModule } from '../gemgridcell';

@NgModule({
  "declarations": [GemGrid], "imports": [CommonModule, GemgridcellModule], "exports": [GemGrid]
})
export class GemgridModule {
}