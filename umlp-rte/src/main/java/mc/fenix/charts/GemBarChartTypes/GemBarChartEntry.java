/* (c) https://github.com/MontiCore/monticore */
package mc.fenix.charts.GemBarChartTypes;

import java.util.List;
import java.util.Optional;

public class GemBarChartEntry extends GemBarChartEntryTOP {
  public GemBarChartEntry() {
    super();
  }

  public GemBarChartEntry(Optional<String> label, Optional<String> stack, List<Integer> data,
      Optional<String> backgroundColor, Optional<String> hoverBackgroundColor) {
    this.label = label;
    this.stack = stack;
    this.data = data;
    this.backgroundColor = backgroundColor;
    this.hoverBackgroundColor = hoverBackgroundColor;
  }

  public List<Integer> getData() {
    return this.getDataList();
  }
}
