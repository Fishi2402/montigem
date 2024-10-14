import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { GemIcon } from './gem-icon.component';
import {MatIconModule} from "@angular/material/icon";

@NgModule({
  declarations: [GemIcon],
  imports: [
    CommonModule,
    MatIconModule
  ],
  exports: [GemIcon]
})
export class GemiconModule  {}
