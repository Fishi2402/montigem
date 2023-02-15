// GEN-P

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { GemButton } from "./gem-button/gem-button.component";
import { GemText } from "./gem-text/gem-text.component";
// added if there is a sub-package which contains components
import { InputModule } from "./input/input-top.module";

@NgModule({
  declarations: [
    GemButton,
    GemText
  ],
  imports: [
    CommonModule,
    // if there is a sub-package which contains components, the sub-package module is imported
    InputModule
  ],
  exports: [
    GemButton,
    GemText,
    // if there is a sub-package which contains components, the sub-package module is exported
    InputModule
  ]
})
export class IoModule { }

