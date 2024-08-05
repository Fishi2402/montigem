import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { GemDropdownInput } from './gem-dropdown-input.component';
import { FormsModule } from "@angular/forms";
import { MatTooltipModule } from "@angular/material/tooltip";
import {InputModeModule} from "../gemtextinput/inputMode/inputMode.module";
import {MatIconModule} from "@angular/material/icon";

@NgModule({
  declarations: [GemDropdownInput],
    imports: [
      CommonModule,
      MatTooltipModule,
      FormsModule,
      InputModeModule,
      MatIconModule
    ],
  exports: [GemDropdownInput]
})
export class GemdropdowninputModule  {}
