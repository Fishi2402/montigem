package gui.pipelines;

import mc.fenix.charts.GemBarChartTypes.GemBarChartData;

class ProjectDataCheck {
  private GemBarChartData value;
  private String hash;
  private boolean calculating;

  public GemBarChartData getValue() {
    return value;
  }

  public void setValue(GemBarChartData value) {
    this.value = value;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public boolean isCalculating() {
    return calculating;
  }

  public void setCalculating(boolean calculating) {
    this.calculating = calculating;
  }
}
