/* (c) https://github.com/MontiCore/monticore */
package example_charts;

import umlp.jsweet.extension.annotation.Component;
import umlp.guiutil.Router;
import java.util.function.Consumer;

@Component
public class ChartsOverview extends ChartsOverviewTOP {

  private Consumer<Void> navigateTo(String target) {
      return v -> {
          Router.navigate("example_charts", target);
      };
  }

  public Consumer<Void> goToBarChart() { return navigateTo("GemBarChartExample"); }
  public Consumer<Void> goToPieChart() { return navigateTo("GemPieChartExample"); }
  public Consumer<Void> goToLineChart() { return navigateTo("GemLineChartExample"); }
  public Consumer<Void> goToGaugeChart() { return navigateTo("GemGaugeChartExample"); }
  public Consumer<Void> goToRadarChart() { return navigateTo("GemRadarChartExample"); }
  public Consumer<Void> goToScatterPlot() { return navigateTo("GemScatterPlotExample"); }
  public Consumer<Void> goToVennDiagram() { return navigateTo("GemVennDiagramExample"); }
  public Consumer<Void> goToNetworkGraph() { return navigateTo("GemGraphChartExample"); }
  public Consumer<Void> goToHeatmap() { return navigateTo("GemHeatmapChartExample"); }
  public Consumer<Void> goToSunburst() { return navigateTo("GemSunburstChartExample"); }
  public Consumer<Void> goToTimeline() { return navigateTo("GemTimelineChartExample"); }
  public Consumer<Void> goToTimeSeries() { return navigateTo("GemTimeSeriesChartExmaple"); }
  public Consumer<Void> goToBoxPlot() { return navigateTo("GemBoxPlotExample"); }
  public Consumer<Void> goToBulletChart() { return navigateTo("GemBulletChartExample"); }
  public Consumer<Void> goToCandlestick() { return navigateTo("GemCandlestickChartExample"); }
}
