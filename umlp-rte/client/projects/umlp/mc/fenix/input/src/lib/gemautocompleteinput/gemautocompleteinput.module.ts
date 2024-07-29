import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from "@angular/forms";
import { MatTooltipModule } from "@angular/material/tooltip";
import { GemAutocompleteInput } from "./gem-autocomplete-input.component";

@NgModule({
  declarations: [GemAutocompleteInput],
  imports: [
    CommonModule,
    MatTooltipModule,
    FormsModule
  ],
  exports: [GemAutocompleteInput]
})
export class GemautocompleteinputModule {}
