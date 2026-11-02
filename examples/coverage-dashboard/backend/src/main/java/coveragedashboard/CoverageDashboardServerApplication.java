package coveragedashboard;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;

import java.io.IOException;

public class CoverageDashboardServerApplication extends CoverageDashboardServerApplicationTOP {
  public static void main(String[] args) {
    SpringApplication.run(CoverageDashboardServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    createDummyData();
  }

  public void createDummyData(){

    CoverageColumnRange range = CoverageDashboardManager.coverageColumnRangeBuilder()
        .fromColumn(0)
        .toColumn(10)
        .covered(true)
        .build().get();

    CoverageLine line = CoverageDashboardManager.coverageLineBuilder()
        .lineNumber(1)
        .missedInstructions(0)
        .coveredInstructions(1)
        .missedBranches(0)
        .coveredBranches(0)
        .columnRangesAdd(range)
        .build().get();


    CoverageDashboardManager.coverageSourceFileBuilder()
        .name("Debug test name")
        .type("java")
        .content("0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789\n0123456789")
        .linesAdd(line)
        .build();

  }
}


