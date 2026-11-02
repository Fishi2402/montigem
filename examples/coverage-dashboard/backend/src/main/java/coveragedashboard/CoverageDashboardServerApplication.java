package coveragedashboard;

import coveragedashboard.ingestion.CoverageIngestionService;
import de.se_rwth.commons.logging.Log;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class CoverageDashboardServerApplication extends CoverageDashboardServerApplicationTOP {
  public static void main(String[] args) {
    SpringApplication.run(CoverageDashboardServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    createLocalData();
  }

  public void createLocalData(){
    // Mock the service and load data by hand for now
    CoverageIngestionService service = new CoverageIngestionService();

    Log.warn(LocalDateTime.now().toString() + ": Begin data-ingestion");
    // Load the front-end data
    File e2eFrontendFile = new File("./../../jsweet-test/frontend/build/app/coverage/coverage-final.json");
    Path basePath = Path.of("./../../jsweet-test/frontend/build/app/dist/client/browser/").toAbsolutePath().normalize();
    CoverageReport e2eReport = service.ingestArtifact(e2eFrontendFile, "Generated Code Test (exaples/jsweet-test)", basePath, ReportType.GENERATED_CODE_TEST);
    Log.warn(LocalDateTime.now().toString() + ": Finished 'Generated Code Test'");

    // Load internal jsweet-test coverage
    File internalJsweetFile = new File("./../../../umlp-jsweet/build/reports/jacoco/test/jacocoTestReport.xml");
    basePath = Path.of("./../../../umlp-jsweet/src/main/java/").toAbsolutePath().normalize();
    CoverageReport internalJSweetReport = service.ingestArtifact(internalJsweetFile, "Generator Unit Test (umlp-jsweet)", basePath, ReportType.GENERATOR_UNIT_TEST);
    Log.warn(LocalDateTime.now().toString() + ": Finished 'Generator Unit Test'");

    // Load jsweet-test umlpjava2ts and gui2java2ts coverage
    File aggregatedTranspilationFile = new File("./../../jsweet-test/frontend/build/reports/jacoco/jacocoAggregated/jacocoAggregated.xml");
    CoverageReport aggregatedTranspilationReport = service.ingestArtifact(aggregatedTranspilationFile, "Generator compilation test (exaples/jsweet-test)", basePath, ReportType.GENERATOR_COMPILATION_TEST);
    Log.warn(LocalDateTime.now().toString() + ": Finished 'Generator compilation test'");

    // Create a run based on this data
    CoverageDashboardManager.runBuilder()
        .commitHash("f0879ff463c32226eea6b39e41860f79b70668d8")
        .timestamp(LocalDateTime.now())
        .branchName("ma-mfl/Generator_coverage")
        .reportsAdd(e2eReport)
        .reportsAdd(internalJSweetReport)
        .reportsAdd(aggregatedTranspilationReport)
        .build().get();
  }
}


