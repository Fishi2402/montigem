import { PlotlyViaCDNModule } from 'angular-plotly.js';
import { ChartOptionsModule } from "../chart-options/chart-options.module"
import { GemchartlegendModule } from '../gemchartlegend/gemchartlegend.module-gen'

export const imports = [
  GemchartlegendModule,
  ChartOptionsModule,
  PlotlyViaCDNModule
]