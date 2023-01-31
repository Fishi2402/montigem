import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { GemButton } from "./gem-button/gem-button.component";
import { InputModule } from "./gem-input/input.module";

@NgModule({
  declarations: [
    GemButton
  ],
  imports: [
    CommonModule,
    InputModule
  ],
  exports: [
    GemButton,
    InputModule
  ]
})
export class IoModule { }

