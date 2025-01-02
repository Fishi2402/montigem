package domain.commands;

import mc.fenix.charts.gembarcharttypes.GemBarChartData;
import mc.fenix.charts.gembarcharttypes.GemBarChartDataBuilder;
import mc.fenix.charts.gembarcharttypes.GemBarChartEntryBuilder;
import umlp.backendrte.command.SimpleResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.List;
import java.util.Optional;

public class GetBarChart extends GetBarChartTOP {

  public GetBarChart(String val) {
    super(val);
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  @Override
  public SimpleResult<GemBarChartData> doAction() {
    return new SimpleResult<>(getId(), new GemBarChartDataBuilder().entries(
        List.of(new GemBarChartEntryBuilder().label(Optional.of("No Label")).data(List.of(100,299,300,400)).build().get()))
        .labels(List.of("No Label")).build().get(),
        "mc.fenix.charts.gembarcharttypes.GemBarChartData");
  }
}
