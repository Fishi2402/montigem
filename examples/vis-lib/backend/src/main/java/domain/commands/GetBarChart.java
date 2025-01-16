package domain.commands;

import domain.DomainManager;
import domain.Person;
import mc.fenix.charts.gembarcharttypes.GemBarChartData;
import mc.fenix.charts.gembarcharttypes.GemBarChartDataBuilder;
import mc.fenix.charts.gembarcharttypes.GemBarChartEntryBuilder;
import umlp.backendrte.command.SimpleResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GetBarChart extends GetBarChartTOP {

  public GetBarChart(List<Long> persons, String val) {
    super(persons, val);
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  @Override
  public SimpleResult<GemBarChartData> doAction() {

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
    return new SimpleResult<>(getId(), new GemBarChartDataBuilder().entries(
        List.of(new GemBarChartEntryBuilder().label(Optional.empty()).data(new ArrayList<>(nameToOccurrence.values())).build().get()))
        .labels(new ArrayList<>(nameToOccurrence.keySet())).build().get(),
        "mc.fenix.charts.gembarcharttypes.GemBarChartData");
  }
}
