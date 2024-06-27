/* (c) https://github.com/MontiCore/monticore */
package mc.fenix.charts.GemBarChartTypes;

import java.util.List;

public class GemBarChartData extends GemBarChartDataTOP {

  public GemBarChartData() {
  }

  public GemBarChartData(List<GemBarChartEntry> entries, List<String> labels) {
    this.entries = entries;
    this.labels = labels;
  }

  public List<String> getLabels() {
    return this.getLabelsList();
  }

  public List<GemBarChartEntry> getEntries() {
    return this.getEntriesList();
  }
}
