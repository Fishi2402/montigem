package gui.pipelines;

import dtcockpit.PipelineStatistic;
import dtcockpit.Project;
import jsweet.lang.Replace;
import mc.fenix.charts.gembarcharttypes.GemBarChartData;
import mc.fenix.charts.gembarcharttypes.GemBarChartEntry;

import java.sql.Date;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class PipelineStats extends PipelineStatsTOP {

  private boolean disableButton = false;
  private int maxValue = 0;
  private boolean stacked = true;

  @Override
  public GemBarChartData getBarChartForPipelineRunsData() {
    List<String> labels = new ArrayList<>();
    Map<String, List<Integer>> entries = new HashMap<>();
    entries.put("Successful", new ArrayList<>());
    entries.put("Unsuccessful", new ArrayList<>());
    for (PipelineStatistic stat : this.getP().getPStatisticsList()) {
      String label = formatDate(stat.getTime());
      int index = labels.indexOf(label);
      if (index == -1) {
        labels.add(label);
        index = labels.indexOf(label);
        entries.get("Successful").add(0);
        entries.get("Unsuccessful").add(0);
      }
      if (stat.isSuccessful()) {
        entries.get("Successful").set(index, entries.get("Successful").get(index) + 1);
      } else {
        entries.get("Unsuccessful").set(index, entries.get("Unsuccessful").get(index) + 1);
      }
    }

    for (int i = 0; i < entries.get("Successful").size(); i++) {
      if (this.stacked) {
        if (this.maxValue < entries.get("Successful").get(i) + entries.get("Unsuccessful").get(i) + 5) {
          this.maxValue = entries.get("Successful").get(i) + entries.get("Unsuccessful").get(i) + 5;
        }
      } else {
        if (this.maxValue < entries.get("Successful").get(i) + 5) {
          this.maxValue = entries.get("Successful").get(i) + 5;
        }
        if (this.maxValue < entries.get("Unsuccessful").get(i) + 5) {
          this.maxValue = entries.get("Unsuccessful").get(i) + 5;
        }
      }
    }

    List<GemBarChartEntry> barChartEntries = new ArrayList<>();
    for (Map.Entry<String, List<Integer>> entry : entries.entrySet()) {
      String label = entry.getKey();
      List<Integer> value = entry.getValue();
      barChartEntries.add(new GemBarChartEntry(Optional.of(label),
          Optional.empty(),
          value,
          Optional.of(label.equals(
              "Successful") ?
              "#57ab27" :
              "#cc071e"),
          Optional.empty()));
    }
    GemBarChartData data = new GemBarChartData(barChartEntries, labels);

    return data;
  }

  @Replace("return `${date.getDate()}.${date.getMonth() + 1}.${date.getFullYear()}`;")
  String formatDate(LocalDate date) {
    return DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMAN).format(Date.valueOf(date));
  }

  @Override
  public Integer getBarChartForPipelineRunsMaxValue() {
    return maxValue;
  }

  @Override
  public Boolean getBarChartForPipelineRunsStacked() {
    return stacked;
  }

  public String getGemButton3Label() {
    if (this.disableButton) {
      return "PipelineInformation already loaded into Backend";
    }
    return "Load PipelineInformation into Backend";
  }

  @Override
  public Consumer<Void> loadPipelineInfoIntoBackend(Project val_$_$0) {
    return $ -> {};
  }
}
