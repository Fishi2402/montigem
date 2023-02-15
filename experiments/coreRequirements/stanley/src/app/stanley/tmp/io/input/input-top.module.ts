// GEN-P

import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { GemTextInput } from "./gem-text-input/gem-text-input.component";
import { GemNumberInput } from "./gem-number-input/gem-number-input.component";
// imported if HWC-P file is present
import { module } from "./input.module";

@NgModule({
  declarations: [
    GemTextInput,
    GemNumberInput,
    // this code is only generated if HWC-P file is present 
    ...(module.declarations ? module.declarations : [])
  ],
  imports: [
    CommonModule,
    ...(module.imports ? module.imports : [])
  ],
  exports: [
    GemTextInput,
    GemNumberInput,
    ...(module.exports ? module.exports : [])
  ],
  providers: [
    ...(module.providers ? module.providers : [])
  ]
})
export class InputModule { }

