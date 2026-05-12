/* (c) https://github.com/MontiCore/monticore */
package manual;

import mc.fenix.charts.gemradarcharttypes.GemRadarChartData;
import mc.fenix.charts.gemradarcharttypes.GemRadarChartDataBuilder;
import mc.fenix.charts.gemradarcharttypes.GemRadarChartDataSetBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class RadarChartTest extends RadarChartTestTOP {

  @Override
  public void init() {
    super.init();
  }

  @Override
  public GemRadarChartData getData() {
    GemRadarChartData data = new GemRadarChartDataBuilder()
        .labels(Arrays.asList("Kategorie 1", "Kategorie 2", "Kategorie 3", "Kategorie 4", "Kategorie 5"))
        .dataSets(Arrays.asList(
            new GemRadarChartDataSetBuilder()
                .label("Current State")
                .data(Arrays.asList(3d, 2d, 4d, 1d, 2d))
                .backgroundColor(Optional.of("rgba(246, 168, 0, 0.39)"))
                .borderColor(Optional.of("rgba(246, 168, 0, 1)"))
                .fill(Optional.of(true))
                .build().get(),
            new GemRadarChartDataSetBuilder()
                .label("Target State")
                .data(Arrays.asList(5d, 3d, 4d, 2d, 3d))
                .backgroundColor(Optional.of("rgba(87, 171, 39, 0.46)"))
                .borderColor(Optional.of("rgba(150, 222, 108, 1)"))
                .fill(Optional.of(true))
                .build().get()
        )).build().get();
    return data;
  }

  public Consumer<Void> changeWidth(int delta) {
    return v -> {
      int currentWidth = Integer.parseInt(getWidth().replace("px", ""));
      int newWidth = currentWidth + delta;
      if (newWidth < 100) newWidth = 100;
      setWidth(newWidth + "px");
    };
  }

  public Consumer<Void> changeHeight(int delta) {
    return v -> {
      int currentHeight = Integer.parseInt(getHeight().replace("px", ""));
      int newHeight = currentHeight + delta;
      if (newHeight < 100) newHeight = 100;
      setHeight(newHeight + "px");
    };
  }
}
