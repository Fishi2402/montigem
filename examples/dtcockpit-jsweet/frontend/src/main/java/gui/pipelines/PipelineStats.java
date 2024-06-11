/* (c) https://github.com/MontiCore/monticore */
package gui.pipelines;

import de.se_rwth.commons.logging.Log;
import dtcockpit.PipelineStatistic;
import dtcockpit.Project;
import jsweet.lang.Replace;
import mc.fenix.charts.GemBarChartTypes.GemBarChartData;
import mc.fenix.charts.GemBarChartTypes.GemBarChartEntry;
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

@Component
public class PipelineStats extends PipelineStatsTOP {

  // TODO: JSweet initial value missing (new ProjectDataCheck()) #3968
  private final ProjectDataCheck projectDataCheck = new ProjectDataCheck();

  // TODO: JSweet initial value missing (=0  !== undefined)
  private int maxValue = 0;

  public PipelineStats(Project p) {
    setP(p);
  }

  @Override
  public String getGemCard0Title() {
    if (getP() == null) return "Loading... (project is null)";
    return "Pipeline dashboard for " + this.getP().getName();
  }

  @Override
  public GemBarChartData getBarChartForPipelineRunsData() {
    Project project = getP();
    if (project == null) {
      Log.warn("#3967 - p variable might be updated at a later point, but getBarChartForPipelineRunsData never updated accordingly");
      return null;
    }

    if (this.projectDataCheck == null)
      Log.error("Please fix #3968 - projectDataCheck is not set to any initial value");

    List<String> labels = new ArrayList<>();
    Map<String, List<Integer>> entries = new HashMap<>();
    List<Integer> successful = new ArrayList<>();
    List<Integer> unsuccessful = new ArrayList<>();
    entries.put("Successful", successful);
    entries.put("Unsuccessful", unsuccessful);

    for (PipelineStatistic stat : project.getPStatistics()) {
      String label = formatDate(stat.getTime());
      int index = labels.indexOf(label);
      if (index == -1) {
        index = labels.size();
        labels.add(label);
        successful.add(0);
        unsuccessful.add(0);
      }
      if (stat.isSuccessful()) {
        successful.set(index, successful.get(index) + 1);
      } else {
        unsuccessful.set(index, unsuccessful.get(index) + 1);
      }
    }
    for (int i = 0; i < successful.size(); i++) {
      Integer currentMaxValue = getBarChartForPipelineRunsMaxValue();
      if (getBarChartForPipelineRunsStacked()) {
        this.maxValue = Math.max(currentMaxValue, successful.get(i) + unsuccessful.get(i) + 5);
      } else {
        this.maxValue = Math.max(currentMaxValue,
                                 Math.max(successful.get(i), unsuccessful.get(i)) + 5);
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
    String newHash = data.toString();
    if (projectDataCheck.getHash() == null || projectDataCheck.getHash().equals(newHash)) {
      projectDataCheck.setHash(newHash);
      projectDataCheck.setValue(data);
    }

    return projectDataCheck.getValue();
  }

  @Replace("return `${date.getDate()}.${date.getMonth() + 1}.${date.getFullYear()}`;")
  String formatDate(LocalDate date) {
    return DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.GERMAN).format(Date.valueOf(date));
  }

  @Override
  public Integer getBarChartForPipelineRunsMaxValue() {
    return maxValue;
  }

  // TODO Other methods
}
