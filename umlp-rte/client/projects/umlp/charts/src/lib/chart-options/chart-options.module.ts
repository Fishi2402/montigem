import { ChartOptionsComponent } from './chart-options.component';
import { OverlayModule } from '@angular/cdk/overlay'; 
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

@NgModule({
  declarations: [
    ChartOptionsComponent
  ],
  imports: [
    CommonModule,
    OverlayModule
  ],
  exports: [
    ChartOptionsComponent
  ]
})
export class ChartOptionsModule { }
