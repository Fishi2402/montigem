package domain;

import mc.fenix.charts.gembarcharttypes.GemBarChartData;

import java.util.Optional;

public class Data extends DataTOP {
  @Override
  public Optional<GemBarChartData> getBcData() {
    return Optional.empty();
  }
}
