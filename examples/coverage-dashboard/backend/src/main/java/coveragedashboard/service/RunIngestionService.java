package coveragedashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageReport;
import coveragedashboard.Run;
import coveragedashboard.RunBuilder;
import coveragedashboard.TestGroup;
import coveragedashboard.TestGroupBuilder;
import coveragedashboard.ingestion.CoverageIngestionService;
import coveragedashboard.service.dto.IngestionManifest;
import coveragedashboard.service.dto.RunIngestionResponse;
import coveragedashboard.util.CciCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Orchestrates coverage bundle extraction, {@code manifest.json} validation, report ingestion,
 * and {@link Run} persistence.
 */
@Service
public class RunIngestionService {

  static final String MANIFEST_FILE_NAME = "manifest.json";

  private final ArtifactStorageService storageService;
  private final ObjectMapper objectMapper;
  private final CoverageIngestionService coverageIngestionService;

  @Autowired
  public RunIngestionService(ArtifactStorageService storageService, ObjectMapper objectMapper) {
    this(storageService, objectMapper, new CoverageIngestionService());
  }

  RunIngestionService(
      ArtifactStorageService storageService,
      ObjectMapper objectMapper,
      CoverageIngestionService coverageIngestionService) {
    this.storageService = storageService;
    this.objectMapper = objectMapper;
    this.coverageIngestionService = coverageIngestionService;
  }

  public synchronized RunIngestionResponse ingestArchive(MultipartFile archive) {
    Path currentDir = storageService.extractToRunDirectory(archive);
    try {
      IngestionManifest manifest = readAndValidateManifest(currentDir);
      currentDir = storageService.moveToCommitDirectory(currentDir, manifest.commitHash());
      Run run = ingestManifest(currentDir, manifest);
      return toResponse(run);
    } catch (RuntimeException e) {
      storageService.deleteQuietly(currentDir);
      throw e;
    }
  }

  public Optional<RunIngestionResponse> getRunSummary(long runId) {
    return CoverageDashboardManager.runOpt(runId).map(this::toResponse);
  }

  private IngestionManifest readAndValidateManifest(Path runDir) {
    Path manifestPath = runDir.resolve(MANIFEST_FILE_NAME);
    if (!Files.isRegularFile(manifestPath)) {
      throw new InvalidIngestionRequestException(
          "Archive must contain a '" + MANIFEST_FILE_NAME + "' file at its root.");
    }

    IngestionManifest manifest;
    try {
      manifest = objectMapper.readValue(manifestPath.toFile(), IngestionManifest.class);
    } catch (IOException e) {
      throw new InvalidIngestionRequestException("Invalid " + MANIFEST_FILE_NAME + ": " + e.getMessage(), e);
    }

    validateManifest(runDir, manifest);
    return manifest;
  }

  private void validateManifest(Path runDir, IngestionManifest manifest) {
    if (manifest == null) {
      throw new InvalidIngestionRequestException("Manifest must not be null.");
    }
    if (isBlank(manifest.commitHash())) {
      throw new InvalidIngestionRequestException("Manifest field 'commitHash' is required.");
    }
    if (isBlank(manifest.branchName())) {
      throw new InvalidIngestionRequestException("Manifest field 'branchName' is required.");
    }
    if (manifest.testGroups() == null || manifest.testGroups().isEmpty()) {
      throw new InvalidIngestionRequestException("Manifest must contain at least one test group.");
    }

    for (IngestionManifest.TestGroupEntry group : manifest.testGroups()) {
      if (group == null || isBlank(group.name())) {
        throw new InvalidIngestionRequestException("Each test group must have a non-blank 'name'.");
      }
      if (group.reports() == null || group.reports().isEmpty()) {
        throw new InvalidIngestionRequestException(
            "Test group '" + group.name() + "' must contain at least one report.");
      }
      for (IngestionManifest.ReportEntry report : group.reports()) {
        if (report == null) {
          throw new InvalidIngestionRequestException("Report entry must not be null.");
        }
        if (isBlank(report.name())) {
          throw new InvalidIngestionRequestException(
              "Each report in test group '" + group.name() + "' must have a non-blank 'name'.");
        }
        if (report.type() == null) {
          throw new InvalidIngestionRequestException(
              "Report '" + report.name() + "' must specify a valid 'type'.");
        }
        if (isBlank(report.reportPath())) {
          throw new InvalidIngestionRequestException(
              "Report '" + report.name() + "' must specify 'reportPath'.");
        }
        if (isBlank(report.basePath())) {
          throw new InvalidIngestionRequestException(
              "Report '" + report.name() + "' must specify 'basePath'.");
        }

        Path reportFile = storageService.resolveSafePath(runDir, report.reportPath());
        if (!Files.isRegularFile(reportFile)) {
          throw new InvalidIngestionRequestException(
              "Report file referenced in manifest does not exist in archive: " + report.reportPath());
        }
        if (report.weight() != null && (!Double.isFinite(report.weight()) || report.weight() <= 0.0)) {
          throw new InvalidIngestionRequestException(
              "Report '" + report.name() + "' weight must be a finite positive number, got: " + report.weight());
        }
        if (report.perspectiveWeights() != null) {
          for (Map.Entry<String, Double> pw : report.perspectiveWeights().entrySet()) {
            Double val = pw.getValue();
            if (val == null || !Double.isFinite(val) || val <= 0.0) {
              throw new InvalidIngestionRequestException(
                  "Report '" + report.name() + "' perspectiveWeight for '" + pw.getKey()
                      + "' must be a finite positive number, got: " + val);
            }
          }
        }
      }
    }
  }

  private Run ingestManifest(Path runDir, IngestionManifest manifest) {
    List<TestGroup> persistedGroups = new ArrayList<>();
    List<CoverageReport> allReports = new ArrayList<>();
    List<Double> allReportWeights = new ArrayList<>();
    boolean hasAnyReportWeight = false;

    for (IngestionManifest.TestGroupEntry groupEntry : manifest.testGroups()) {
      TestGroupBuilder groupBuilder = CoverageDashboardManager.testGroupBuilder()
          .name(groupEntry.name());

      List<CoverageReport> groupReports = new ArrayList<>();
      List<Double> groupWeights = new ArrayList<>();
      boolean hasGroupWeight = false;

      for (IngestionManifest.ReportEntry reportEntry : groupEntry.reports()) {
        Path reportFile = storageService.resolveSafePath(runDir, reportEntry.reportPath());
        Path effectiveBasePath = resolveBasePath(runDir, reportEntry);

        CoverageReport report = coverageIngestionService.ingestArtifact(
            reportFile.toFile(),
            reportEntry.name(),
            effectiveBasePath,
            runDir,
            reportEntry.type(),
            reportEntry.perspectiveWeights()
        );
        if (report == null) {
          throw new InvalidIngestionRequestException(
              "Failed to ingest report '" + reportEntry.name() + "' from " + reportEntry.reportPath());
        }
        groupBuilder.reportsAdd(report);
        groupReports.add(report);
        allReports.add(report);

        double effectiveWeight = reportEntry.weight() != null ? reportEntry.weight() : 1.0;
        if (reportEntry.weight() != null) {
          hasGroupWeight = true;
          hasAnyReportWeight = true;
        }
        groupWeights.add(effectiveWeight);
        allReportWeights.add(effectiveWeight);
      }

      double groupCci = CciCalculator.computeTestGroupCci(
          groupReports, hasGroupWeight ? groupWeights : null);
      groupBuilder.cci(groupCci);

      persistedGroups.add(groupBuilder.build().get());
    }

    double runCci = CciCalculator.computeRunCci(
        allReports, hasAnyReportWeight ? allReportWeights : null);

    LocalDateTime timestamp = manifest.timestamp() != null ? manifest.timestamp() : LocalDateTime.now();
    RunBuilder runBuilder = CoverageDashboardManager.runBuilder()
        .commitHash(manifest.commitHash())
        .branchName(manifest.branchName())
        .timestamp(timestamp)
        .cci(runCci);

    for (TestGroup group : persistedGroups) {
      runBuilder.testGroupsAdd(group);
    }

    return runBuilder.build().get();
  }

  /**
   * Resolves {@code basePath} according to the report format:
   * <ul>
   *   <li>C8 JSON reports compare relative paths against {@code basePath} directly and resolve physical
   *       files against {@code rootPath} ({@code runDir}).</li>
   *   <li>JaCoCo XML reports resolve source files directly via {@code basePath.resolve(relativeSourceFile)},
   *       so {@code basePath} must point into {@code runDir}.</li>
   * </ul>
   */
  Path resolveBasePath(Path runDir, IngestionManifest.ReportEntry reportEntry) {
    String lowerReportPath = reportEntry.reportPath().toLowerCase(Locale.ROOT);
    if (lowerReportPath.endsWith(".json")) {
      return Path.of(reportEntry.basePath());
    }
    return storageService.resolveSafePath(runDir, reportEntry.basePath());
  }

  private RunIngestionResponse toResponse(Run run) {
    List<RunIngestionResponse.TestGroupSummary> groupSummaries = run.getTestGroupsList().stream()
        .map(group -> new RunIngestionResponse.TestGroupSummary(
            group.getName(),
            group.getCci(),
            group.getReportsList().stream()
                .map(report -> new RunIngestionResponse.ReportSummary(
                    report.getName(), report.getType(), report.getCoverageValue()))
                .toList()
        ))
        .toList();

    return new RunIngestionResponse(
        run.getGemId(),
        run.getCommitHash(),
        run.getBranchName(),
        run.getTimestamp(),
        run.getCci(),
        groupSummaries
    );
  }

  private static boolean isBlank(String s) {
    return s == null || s.isBlank();
  }
}
