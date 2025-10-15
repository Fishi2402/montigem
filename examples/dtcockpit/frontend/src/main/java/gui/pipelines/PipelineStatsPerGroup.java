package gui.pipelines;/* (c) https://github.com/MontiCore/monticore */

import dtcockpit.AggregatedPipelineStatisticsOverDate;
import jsweet.lang.Replace;
import mc.fenix.charts.gembarcharttypes.GemBarChartData;
import mc.fenix.charts.gembarcharttypes.GemBarChartEntry;
import mc.fenix.charts.gembarcharttypes.GemBarChartEntryBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.sql.Date;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component()
public class PipelineStatsPerGroup extends PipelineStatsPerGroupTOP {

  private boolean disableButton = false;
  private int maxValue = 0;
  private boolean stacked = true;

  @Replace("return `${date.getDate()}.${date.getMonth() + 1}.${date.getFullYear()}`;")
  String formatDate(LocalDate date) {
    return DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMAN).format(Date.valueOf(date));
  }

  public GemBarChartData getBarChartForPipelineRunsData() {
    List<String> labels = new ArrayList<>();
    Map<String, List<Integer>> entries = new HashMap<>();
    entries.put("Successful", new ArrayList<>());
    entries.put("Unsuccessful", new ArrayList<>());
    for (AggregatedPipelineStatisticsOverDate stat : this.getG().getStatisticsList()) {
      //console.debug("Statistic: ", stat)
      String label = formatDate(stat.getTime());
      int index = labels.indexOf(label);
      if (index == -1) {
        labels.add(label);
      }
    }

    for (String l : labels) {
      entries.get("Successful").add(0);
      entries.get("Unsuccessful").add(0);
    }


    for (AggregatedPipelineStatisticsOverDate stat : this.getG().getStatisticsList()) {
      String label = formatDate(stat.getTime());
      int index = labels.indexOf(label);
      if (index != -1) {
        entries.get("Successful").set(index,
            entries.get("Successful").get(index) + stat.getNumberOfSuccessfulRuns());
        entries.get("Unsuccessful").set(index,
            entries.get("Unsuccessful").get(index) + stat.getNumberOfUnsuccessfulRuns());
      }
    }

    List<GemBarChartEntry> barChartEntries = new ArrayList<>();
    this.maxValue = 0;

    for (int i = 0; i < entries.get("Successful").size(); i++) {
      if (this.stacked) {
        if (this.maxValue < entries.get("Successful").get(i) + entries.get("Unsuccessful").get(i) + 5) {
          this.maxValue = entries.get("Successful").get(i) + entries.get("Unsuccessful").get(i) + 5;
        }
      }
      else {
        if (this.maxValue < entries.get("Successful").get(i) + 5) {
          this.maxValue = entries.get("Successful").get(i) + 5;
        }
        if (this.maxValue < +entries.get("Unsuccessful").get(i)) {
          this.maxValue = entries.get("Unsuccessful").get(i) + 5;
        }
      }
    }

    for (Map.Entry<String, List<Integer>> entry : entries.entrySet()) {
      Optional<GemBarChartEntry> e = new GemBarChartEntryBuilder()
          .label(Optional.of(entry.getKey()))
          .data(entry.getValue())
          .build();
      if (e.isPresent()) {
        barChartEntries.add(e.get());
      }
    }
    GemBarChartData data = new GemBarChartData(barChartEntries, labels);
    return data;
  }

  @Override
  public Boolean getBarChartForPipelineRunsStacked() {
    return this.stacked;
  }

  @Override
  public String getStackedToggleLabel() {
    if (this.stacked) {
      return "Balken nebeneinander anzeigen";
    }
    return "Balken übereinander anzeigen";
  }

}
