package domain.commands;

import domain.DomainManager;
import mc.fenix.charts.gembarcharttypes.GemBarChartDataBuilder;
import mc.fenix.charts.gembarcharttypes.GemBarChartEntryBuilder;
import mc.fenix.charts.gempiecharttypes.GemPieChartData;
import mc.fenix.charts.gempiecharttypes.GemPieChartDataBuilder;
import mc.fenix.charts.gempiecharttypes.GemPieChartEntryBuilder;
import umlp.backendrte.command.SimpleResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GetPieChart extends GetPieChartTOP {

  public GetPieChart(List<Long> persons, String val) {
    super(persons, val);
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  @Override
  public SimpleResult<GemPieChartData> doAction() {

    Map<String, Integer> nameToOccurrence = new LinkedHashMap<>();
    this.getPersonsGemIdList()
        .stream().map(p -> DomainManager.getPersonSure(p)).
        forEach(p -> {
          if(nameToOccurrence.containsKey(p.getName())) {
            nameToOccurrence.put(p.getName(), nameToOccurrence.get(p.getName())+1);
          } else {
            nameToOccurrence.put(p.getName(), 1);
          }
        });
    return new SimpleResult<>(getId(), new GemPieChartDataBuilder().entries(
            nameToOccurrence.entrySet().stream().map(e -> new GemPieChartEntryBuilder().label(e.getKey()).value(e.getValue())).collect(Collectors.toList())).build().get(),
        "mc.fenix.charts.gempiecharttypes.GemPieChartData");
  }
}
