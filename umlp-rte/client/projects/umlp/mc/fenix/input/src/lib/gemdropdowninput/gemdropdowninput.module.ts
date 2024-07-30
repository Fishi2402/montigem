import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { GemDropdownInput } from './gem-dropdown-input.component';
import { FormsModule } from "@angular/forms";
import { MatTooltipModule } from "@angular/material/tooltip";

@NgModule({
  declarations: [GemDropdownInput],
  imports: [
    CommonModule,
    MatTooltipModule,
    FormsModule
  ],
  exports: [GemDropdownInput]
})
export class GemdropdowninputModule  {}
