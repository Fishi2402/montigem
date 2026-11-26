package coveragedashboard.commands;

import coveragedashboard.AggregatedCoverageReport;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.Run;
import coveragedashboard.TestGroup;
import mc.fenix.charts.gemlinecharttypes.GemLineChartData;
import mc.fenix.charts.gemlinecharttypes.GemLineChartDataBuilder;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntry;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntryBuilder;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.GemList;
import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GetCciTrajectory extends GetCciTrajectoryTOP {

  public GetCciTrajectory() {
    super();
  }

  @Override
  public SimpleResult<GemLineChartData> doAction() {
    Optional<GemLineChartData> result = calculateCciTrajectory();
    if (result.isEmpty()) {
      return new SimpleResult<>(
          getId(), LoadingState.FAILED_TO_LOAD, GemLineChartData.class.getName());
    }
    return new SimpleResult<>(getId(), result.get(), GemLineChartData.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<GemLineChartData> calculateCciTrajectory() {
    GemList<Run> runs = CoverageDashboardManager.getRunList();
    List<Run> sortedRuns = runs.stream()
        .sorted(Comparator.comparing(Run::getTimestamp, Comparator.nullsFirst(Comparator.naturalOrder())))
        .toList();

    List<String> labels = new ArrayList<>(sortedRuns.size());
    GemLineChartEntryBuilder runCciBuilder = new GemLineChartEntryBuilder()
        .label("Run CCI (%)")
        .borderColor("#2563eb")
        .pointBackgroundColor("#2563eb");

    Map<String, GemLineChartEntryBuilder> mergedBuilders = new LinkedHashMap<>();
    Map<String, GemLineChartEntryBuilder> groupBuilders = new LinkedHashMap<>();

    for (Run run : sortedRuns) {
      labels.add(formatLabel(run));
      runCciBuilder.addData(toPercent(run.getCci()));

      Set<String> seenMerged = new HashSet<>();
      for (AggregatedCoverageReport merged : run.getAggregatedReportsList()) {
        String seriesName = merged.getName() + " (%)";
        seenMerged.add(seriesName);
        GemLineChartEntryBuilder builder = mergedBuilders.computeIfAbsent(seriesName, name -> {
          GemLineChartEntryBuilder b = new GemLineChartEntryBuilder().label(name);
          for (int i = 0; i < labels.size() - 1; i++) {
            b.addData(0.0);
          }
          return b;
        });
        builder.addData(toPercent(merged.getCoverageValue()));
      }
      for (Map.Entry<String, GemLineChartEntryBuilder> entry : mergedBuilders.entrySet()) {
        if (!seenMerged.contains(entry.getKey())) {
          entry.getValue().addData(0.0);
        }
      }

      Set<String> seenGroups = new HashSet<>();
      for (TestGroup group : run.getTestGroupsList()) {
        String seriesName = group.getName() + " (%)";
        seenGroups.add(seriesName);
        GemLineChartEntryBuilder builder = groupBuilders.computeIfAbsent(seriesName, name -> {
          GemLineChartEntryBuilder b = new GemLineChartEntryBuilder().label(name);
          for (int i = 0; i < labels.size() - 1; i++) {
            b.addData(0.0);
          }
          return b;
        });
        builder.addData(toPercent(group.getCci()));
      }
      for (Map.Entry<String, GemLineChartEntryBuilder> entry : groupBuilders.entrySet()) {
        if (!seenGroups.contains(entry.getKey())) {
          entry.getValue().addData(0.0);
        }
      }
    }

    List<GemLineChartEntry> entries = new ArrayList<>();
    entries.add(runCciBuilder.build().get());
    for (GemLineChartEntryBuilder builder : mergedBuilders.values()) {
      entries.add(builder.build().get());
    }
    for (GemLineChartEntryBuilder builder : groupBuilders.values()) {
      entries.add(builder.build().get());
    }

    GemLineChartData data = new GemLineChartDataBuilder()
        .entries(entries)
        .labels(labels)
        .build()
        .get();

    return Optional.of(data);
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected String formatLabel(Run run) {
    String timePart = run.getTimestamp() != null
        ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(run.getTimestamp())
        : "unknown";
    String hash = run.getCommitHash();
    String shortHash = (hash == null || hash.isEmpty())
        ? "unknown"
        : (hash.length() > 8 ? hash.substring(0, 8) : hash);
    return timePart + " (" + shortHash + ")";
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected double toPercent(double ratio) {
    if (Double.isNaN(ratio) || Double.isInfinite(ratio)) {
      return 0.0;
    }
    return Math.round(ratio * 10000.0) / 100.0;
  }
}
