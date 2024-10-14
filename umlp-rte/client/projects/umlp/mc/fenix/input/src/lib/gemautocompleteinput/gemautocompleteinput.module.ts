import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from "@angular/forms";
import { MatTooltipModule } from "@angular/material/tooltip";
import { GemAutocompleteInput } from "./gem-autocomplete-input.component";
import {InputModeModule} from "../gemtextinput/inputMode/inputMode.module";
import {MatIcon} from "@angular/material/icon";

@NgModule({
  declarations: [GemAutocompleteInput],
    imports: [
        CommonModule,
        MatTooltipModule,
        FormsModule,
        InputModeModule,
        MatIcon
    ],
  exports: [GemAutocompleteInput]
})
export class GemautocompleteinputModule {}
