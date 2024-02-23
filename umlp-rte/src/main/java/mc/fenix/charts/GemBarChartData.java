/* (c) https://github.com/MontiCore/monticore */
package mc.fenix.charts;

import java.util.List;

public class GemBarChartData {

  private List<GemBarChartEntry> entries;
  private List<String> labels;

  public GemBarChartData(List<GemBarChartEntry> entries, List<String> labels) {
    this.entries = entries;
    this.labels = labels;
  }

  public List<GemBarChartEntry> getEntries() {
    return entries;
  }

  public void setEntries(List<GemBarChartEntry> entries) {
    this.entries = entries;
  }

  public List<String> getLabels() {
    return labels;
  }

  public void setLabels(List<String> labels) {
    this.labels = labels;
  }
}
