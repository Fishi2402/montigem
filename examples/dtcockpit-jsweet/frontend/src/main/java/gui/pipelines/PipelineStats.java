/* (c) https://github.com/MontiCore/monticore */
package gui.pipelines;

import dtcockpit.Project;
import mc.fenix.charts.GemBarChartData;
import mc.fenix.charts.GemBarChartEntry;
import umlp.jsweet.extension.annotation.Component;

import java.sql.Date;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class PipelineStats extends PipelineStatsTOP {

  private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.DEFAULT,
                                                                           Locale.GERMAN);
  private final ProjectDataCheck projectDataCheck = new ProjectDataCheck();
  private int maxValue = 0;

  public PipelineStats(Project p) {
    super(p);
  }

  @Override
  public String getGemCard0Title() {
    return "Pipeline dashboard for " + this.getP().getName();
  }

  @Override
  public GemBarChartData getBarChartForPipelineRunsData() {
    Project project = getP();
    if (project == null) {
      return null;
    }

    List<String> labels = new ArrayList<>();
    Map<String, List<Integer>> entries = new HashMap<>();
    List<Integer> successful = new ArrayList<>();
    List<Integer> unsuccessful = new ArrayList<>();
    entries.put("Successful", successful);
    entries.put("Unsuccessful", unsuccessful);

    project.getPStatistics().forEach(stat -> {
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
    });
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
    entries.forEach((label, data) -> barChartEntries.add(new GemBarChartEntry(Optional.of(label),
                                                                              Optional.empty(),
                                                                              data,
                                                                              Optional.of(label.equals(
                                                                                  "Successful") ?
                                                                                              "#57ab27" :
                                                                                              "#cc071e"),
                                                                              Optional.empty())));
    GemBarChartData data = new GemBarChartData(barChartEntries, labels);
    String newHash = data.toString();
    if (!Objects.equals(projectDataCheck.hash, newHash)) {
      projectDataCheck.hash = newHash;
      projectDataCheck.value = data;
    }

    return projectDataCheck.value;
  }

  String formatDate(LocalDate date) {
    return DATE_FORMAT.format(Date.valueOf(date));
  }

  @Override
  public Integer getBarChartForPipelineRunsMaxValue() {
    return maxValue;
  }

  // TODO Other methods

  private static class ProjectDataCheck {

    GemBarChartData value;
    String hash;
    boolean calculating;
  }
}
