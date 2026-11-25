package coveragedashboard;

import coveragedashboard.ingestion.CoverageIngestionService;
import coveragedashboard.util.CciCalculator;
import de.se_rwth.commons.logging.Log;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CoverageDashboardServerApplication extends CoverageDashboardServerApplicationTOP {

  @Value("${coverage.ingestion.load-local-data:false}")
  private boolean loadLocalData;

  public static void main(String[] args) {
    SpringApplication.run(CoverageDashboardServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    if (loadLocalData) {
      createLocalData();
    }
  }

  public void createLocalData(){
    // Mock the service and load data by hand for now
    CoverageIngestionService service = new CoverageIngestionService();

    Log.info(LocalDateTime.now().toString() + ": Begin data-ingestion", "CoverageDashboardServerApplication");
    // Load the front-end data
    File e2eFrontendFile = new File("./../../jsweet-test/frontend/build/app/coverage/coverage-final.json");
    Path basePath = Path.of("./examples/jsweet-test/frontend/build/app/dist/client/browser/");
    Path rootPath = Path.of("./../../../");
    CoverageReport e2eReport = service.ingestArtifact(
        e2eFrontendFile,
        "Generated Code Test (examples/jsweet-test) - TS",
        basePath, rootPath, ReportType.GENERATED_CODE_TEST, null);
    Log.info(LocalDateTime.now().toString() + ": Finished 'Generated Code Test (JSweet-Test)'", "CoverageDashboardServerApplication");

    // Load internal jsweet-test coverage
    File internalJsweetFile = new File("./../../../umlp-jsweet/build/reports/jacoco/test/jacocoTestReport.xml");
    basePath = Path.of("./../../../umlp-jsweet/src/main/java/");
    CoverageReport internalJSweetReport = service.ingestArtifact(
        internalJsweetFile,
        "Generator Unit Test (umlp-jsweet) - JAVA",
        basePath, rootPath, ReportType.GENERATOR_UNIT_TEST, null);
    Log.info(LocalDateTime.now().toString() + ": Finished 'Generator Unit Test'", "CoverageDashboardServerApplication");

    // Load jsweet-test umlpjava2ts and gui2java2ts coverage
    File aggregatedTranspilationFile = new File("./../../jsweet-test/frontend/build/reports/jacoco/jacocoAggregated/jacocoAggregated.xml");
    CoverageReport aggregatedTranspilationReport = service.ingestArtifact(
        aggregatedTranspilationFile,
        "Generator compilation test (examples/jsweet-test) - JAVA",
        basePath, rootPath, ReportType.GENERATOR_COMPILATION_TEST, null);
    Log.info(LocalDateTime.now().toString() + ": Finished 'Generator compilation test'", "CoverageDashboardServerApplication");

    // Load assoc-test coverage
    File assocTestFile = new File("./../../it-sync-protocol-test/frontend/build/reports/jacoco/test/jacocoTestReport.xml");
    Path assocBasePath = Path.of("./../../it-sync-protocol-test/backend/build/umlp2java/");
    CoverageReport assocReport = service.ingestArtifact(
        assocTestFile,
        "Generated Code Test (examples/it-sync-protocol-test) - TS",
        assocBasePath, rootPath, ReportType.GENERATED_CODE_TEST, null);
    Log.info(LocalDateTime.now().toString() + ": Finished 'Association Sync Protocol Tests'", "CoverageDashboardServerApplication");

    List<CoverageReport> allReports = new ArrayList<>();

    TestGroup jsweetGroup = null;
    if (e2eReport != null || aggregatedTranspilationReport != null) {
      List<CoverageReport> groupReports = new ArrayList<>();
      TestGroupBuilder jsweetGroupBuilder = CoverageDashboardManager.testGroupBuilder()
          .name("jsweet-test");
      if (e2eReport != null) {
        jsweetGroupBuilder.reportsAdd(e2eReport);
        groupReports.add(e2eReport);
      }
      if (aggregatedTranspilationReport != null) {
        jsweetGroupBuilder.reportsAdd(aggregatedTranspilationReport);
        groupReports.add(aggregatedTranspilationReport);
      }
      allReports.addAll(groupReports);
      jsweetGroup = jsweetGroupBuilder
          .cci(CciCalculator.computeTestGroupCci(groupReports, null))
          .build().get();
    }

    TestGroup umlpJsweetGroup = null;
    if (internalJSweetReport != null) {
      allReports.add(internalJSweetReport);
      umlpJsweetGroup = CoverageDashboardManager.testGroupBuilder()
          .name("umlp-jsweet")
          .reportsAdd(internalJSweetReport)
          .cci(CciCalculator.computeTestGroupCci(List.of(internalJSweetReport), null))
          .build().get();
    }

    TestGroup assocGroup = null;
    if (assocReport != null) {
      allReports.add(assocReport);
      assocGroup = CoverageDashboardManager.testGroupBuilder()
          .name("assoc-test")
          .reportsAdd(assocReport)
          .cci(CciCalculator.computeTestGroupCci(List.of(assocReport), null))
          .build().get();
    }

    // Create a run based on this data
    RunBuilder runBuilder = CoverageDashboardManager.runBuilder()
        .commitHash("f0879ff463c32226eea6b39e41860f79b70668d8")
        .timestamp(LocalDateTime.now())
        .branchName("ma-mfl/Generator_coverage")
        .cci(CciCalculator.computeRunCci(allReports, null));

    if (assocGroup != null) {
      runBuilder.testGroupsAdd(assocGroup);
    }
    if (jsweetGroup != null) {
      runBuilder.testGroupsAdd(jsweetGroup);
    }
    if (umlpJsweetGroup != null) {
      runBuilder.testGroupsAdd(umlpJsweetGroup);
    }

    runBuilder.build().get();
  }
}


