/* (c) https://github.com/MontiCore/monticore */
package mc.fenix.charts.gembarcharttypes;

import java.util.List;
import java.util.Optional;

public class GemBarChartEntry extends GemBarChartEntryTOP {
  public GemBarChartEntry() {
    super();
  }

  public GemBarChartEntry(Optional<String> label, Optional<String> stack, List<Integer> data,
      Optional<String> backgroundColor, Optional<String> hoverBackgroundColor) {
    this._label = label;
    this._stack = stack;
    this._data = data;
    this._backgroundColor = backgroundColor;
    this._hoverBackgroundColor = hoverBackgroundColor;
  }

  public List<Integer> getData() {
    return this.getDataList();
  }
}
