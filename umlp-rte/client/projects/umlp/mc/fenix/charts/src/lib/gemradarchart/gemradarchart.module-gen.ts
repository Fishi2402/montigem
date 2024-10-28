import {CommonModule} from '@angular/common';
import {imports} from './gemradarchart.module';
import {NgModule} from '@angular/core';
import {GemRadarChart} from "./gem-radar-chart.component";

@NgModule(
{
"declarations":[GemRadarChart],"imports":[...imports,CommonModule],"exports":[GemRadarChart]
})
export class GemradarchartModule  {



}
