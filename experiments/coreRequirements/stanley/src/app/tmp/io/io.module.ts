import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { GemButton } from "./gem-button/gem-button.component";
import { InputModule } from "./input/input.module";
import { GemText } from "./gem-text/gem-text.component";

@NgModule({
  declarations: [
    GemButton,
    GemText
  ],
  imports: [
    CommonModule,
    InputModule
  ],
  exports: [
    GemButton,
    GemText,
    InputModule
  ]
})
export class IoModule { }

