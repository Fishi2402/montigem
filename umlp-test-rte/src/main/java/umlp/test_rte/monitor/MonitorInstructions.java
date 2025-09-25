package umlp.test_rte.monitor;

public final class MonitorInstructions {
  private boolean reportActivityEnd = false;

  private MonitorInstructions() {
  }

  public static MonitorInstructions createDefault() {
    return new MonitorInstructions();
  }

  public boolean reportActivityEnd() {
    return reportActivityEnd;
  }

  public MonitorInstructions setReportActivityEnd(boolean reportActivityEnd) {
    this.reportActivityEnd = reportActivityEnd;
    return this;
  }
}
