/* (c) https://github.com/MontiCore/monticore */
package mc.fenix.charts.gembarcharttypes;


import mc.fenix.charts.GemBarChartTypes.GemBarChartEntry;

import java.util.List;

public class GemBarChartData extends GemBarChartDataTOP {

  public GemBarChartData() {
  }

  public GemBarChartData(List<GemBarChartEntry> entries, List<String> labels) {
    this._entries = entries;
    this._labels = labels;
  }

}
