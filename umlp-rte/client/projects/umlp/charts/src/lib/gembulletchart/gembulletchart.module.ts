import { NgChartsModule } from "ng2-charts";
import { PlotlyViaCDNModule } from "angular-plotly.js";

PlotlyViaCDNModule.setPlotlyVersion("latest");

export const imports = [NgChartsModule, PlotlyViaCDNModule];
