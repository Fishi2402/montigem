import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { GemTextInput } from "./gem-text-input/gem-text-input.component";
import { GemNumberInput } from "./gem-number-input/gem-number-input.component";

@NgModule({
  declarations: [
    GemTextInput,
    GemNumberInput
  ],
  imports: [
    CommonModule,
    FormsModule
  ],
  exports: [
    GemTextInput,
    GemNumberInput
  ]
})
export class InputModule { }

