import {CommonModule, NgOptimizedImage} from '@angular/common';
import {NgModule} from '@angular/core';
import {GemStatusIndicator} from "./gem-status-indicator.component";
import {MatTooltipModule} from "@angular/material/tooltip";

@NgModule({
  declarations: [GemStatusIndicator],
  imports: [CommonModule, NgOptimizedImage, MatTooltipModule],
  exports: [GemStatusIndicator]
})
export class GemstatusindicatorModule  {}
