package coveragedashboard.ingestion;

import coveragedashboard.ArtifactCategory;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.CoverageSourceFileBuilder;
import coveragedashboard.test_infrastructure.CoverageDashboardMockTestSetup;
import org.junit.jupiter.api.Test;
import umlp.test_rte.ClientSetup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoverageModuleAggregatorTest extends CoverageDashboardMockTestSetup {

  public CoverageModuleAggregatorTest(){
    super(ClientSetup.of(1));
  }

  @Test
  void testEmptyFileListReturnsOnlyRoot() {
    CoverageModuleAggregator aggregator = new CoverageModuleAggregator();
    CoverageModule rootModule = aggregator.groupFilesInModules(Collections.emptyList());

    assertNotNull(rootModule);
    assertEquals("", rootModule.getName());
    assertTrue(rootModule.getSubModulesList().isEmpty());
    assertTrue(rootModule.getSourceFilesList().isEmpty());
    assertEquals(0, rootModule.getMetrics().getCoveredInstructions());
    assertEquals(0, rootModule.getMetrics().getMissedInstructions());
    assertEquals(0, rootModule.getMetrics().getCoveredBranches());
    assertEquals(0, rootModule.getMetrics().getMissedBranches());
  }

  @Test
  void testTreeBuildingAndMetricRollup() {
    CoverageLine dummyLine = CoverageDashboardManager.measuredLineBuilder()
        .lineNumber(1)
        .coveredInstructions(0).missedInstructions(0)
        .coveredBranches(0).missedBranches(0)
        .build().get();

    CoverageSourceFile rootFileOne = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("RootFileOne.ts")
        .name("RootFileOne.ts")
        .physicalPath("c:/some/path/RootFileOne.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(25).missedInstructions(7)
            .coveredBranches(6).missedBranches(1)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get()
        )
        .linesAdd(dummyLine)
        .build().get();

    CoverageSourceFile rootFileTwo = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("RootFileTwo.ts")
        .name("RootFileTwo.ts")
        .physicalPath("c:/some/path/RootFileTwo.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(100).missedInstructions(0)
            .coveredBranches(12).missedBranches(0)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get()
        )
        .linesAdd(dummyLine)
        .build().get();

    CoverageSourceFile rootFileThree = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("RootFileThree.ts")
        .name("RootFileThree.ts")
        .physicalPath("c:/some/path/RootFileThree.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(10).missedInstructions(40)
            .coveredBranches(2).missedBranches(8)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get()
        )
        .linesAdd(dummyLine)
        .build().get();

    CoverageSourceFile guiAppFile = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("gui/App.ts")
        .name("App.ts")
        .physicalPath("c:/some/path/gui/App.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(200).missedInstructions(15)
            .coveredBranches(20).missedBranches(4)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get()
        )
        .linesAdd(dummyLine)
        .build().get();

    CoverageSourceFile guiButtonFile = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("gui/components/Button.ts")
        .name("Button.ts")
        .physicalPath("c:/some/path/gui/Button.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(50).missedInstructions(0)
            .coveredBranches(5).missedBranches(0)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get()
        )
        .linesAdd(dummyLine)
        .build().get();

    CoverageSourceFile guiTableFile = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("gui/components/Table.ts")
        .name("Table.ts")
        .physicalPath("c:/some/path/gui/components/Table.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(120).missedInstructions(30)
            .coveredBranches(15).missedBranches(5)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get()
        )
        .linesAdd(dummyLine)
        .build().get();

    CoverageSourceFile coreEngineFile = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("core/Engine.ts")
        .name("Engine.ts")
        .physicalPath("c:/some/path/core/Engine.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(500).missedInstructions(100)
            .coveredBranches(50).missedBranches(10)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get()
        )
        .linesAdd(dummyLine)
        .build().get();

    CoverageSourceFile coreMathFile = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("core/utils/MathHelper.ts")
        .name("MathHelper.ts")
        .physicalPath("c:/some/path/core/utils/MathHelper.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(80).missedInstructions(5)
            .coveredBranches(8).missedBranches(2)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get()
        )
        .linesAdd(dummyLine)
        .build().get();

    CoverageSourceFile apiClientFile = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("api/Client.ts")
        .name("Client.ts")
        .physicalPath("c:/some/path/api/Client.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(60).missedInstructions(60)
            .coveredBranches(10).missedBranches(10)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get()
        )
        .linesAdd(dummyLine)
        .build().get();

    List<CoverageSourceFile> mockFiles = Arrays.asList(
        rootFileOne, rootFileTwo, rootFileThree,
        guiAppFile, guiButtonFile, guiTableFile,
        coreEngineFile, coreMathFile,
        apiClientFile
    );

    CoverageModuleAggregator aggregator = new CoverageModuleAggregator();
    CoverageModule rootModule = aggregator.groupFilesInModules(mockFiles);


    // ------------- Root-node -------------
    assertNotNull(rootModule);
    assertEquals("", rootModule.getName());
    assertEquals(3, rootModule.getSourceFilesList().size()); // RootFileOne, RootFileTwo, RootFileThree
    assertEquals(3, rootModule.getSubModulesList().size());  // gui, core, api

    assertEquals(1145, rootModule.getMetrics().getCoveredInstructions()); // 135 + 370 + 580 + 60
    assertEquals(257, rootModule.getMetrics().getMissedInstructions());   // 47 + 45 + 105 + 60
    assertEquals(128, rootModule.getMetrics().getCoveredBranches());      // 20 + 40 + 58 + 10
    assertEquals(40, rootModule.getMetrics().getMissedBranches());        // 9 + 9 + 12 + 10

    // ------------- gui-node -------------
    CoverageModule guiModule = rootModule.getSubModulesList().stream()
        .filter(m -> m.getName().equals("gui")).findFirst().orElseThrow();

    assertEquals(1, guiModule.getSourceFilesList().size()); // App.ts
    assertEquals(1, guiModule.getSubModulesList().size());  // components folder

    // GUI Metrics (App.ts + components folder)
    assertEquals(370, guiModule.getMetrics().getCoveredInstructions()); // 200 + 170
    assertEquals(45, guiModule.getMetrics().getMissedInstructions());   // 15 + 30
    assertEquals(40, guiModule.getMetrics().getCoveredBranches());      // 20 + 20
    assertEquals(9, guiModule.getMetrics().getMissedBranches());        // 4 + 5

    // ------------- gui/components-node -------------
    CoverageModule componentsModule = guiModule.getSubModulesList().stream()
        .filter(m -> m.getName().equals("components")).findFirst().orElseThrow();

    assertEquals(2, componentsModule.getSourceFilesList().size()); // Button.ts, Table.ts
    assertTrue(componentsModule.getSubModulesList().isEmpty());

    assertEquals(170, componentsModule.getMetrics().getCoveredInstructions()); // 50 + 120
    assertEquals(30, componentsModule.getMetrics().getMissedInstructions());   // 0 + 30
    assertEquals(20, componentsModule.getMetrics().getCoveredBranches());      // 5 + 15
    assertEquals(5, componentsModule.getMetrics().getMissedBranches());        // 0 + 5

    // ------------- core-node -------------
    CoverageModule coreModule = rootModule.getSubModulesList().stream()
        .filter(m -> m.getName().equals("core")).findFirst().orElseThrow();

    assertEquals(1, coreModule.getSourceFilesList().size()); // Engine.ts
    assertEquals(1, coreModule.getSubModulesList().size());  // utils folder

    // Core Metrics (Engine.ts + utils folder)
    assertEquals(580, coreModule.getMetrics().getCoveredInstructions()); // 500 + 80
    assertEquals(105, coreModule.getMetrics().getMissedInstructions());  // 100 + 5
    assertEquals(58, coreModule.getMetrics().getCoveredBranches());      // 50 + 8
    assertEquals(12, coreModule.getMetrics().getMissedBranches());       // 10 + 2

    // ------------- core/utils-node -------------
    CoverageModule utilsModule = coreModule.getSubModulesList().stream()
        .filter(m -> m.getName().equals("utils")).findFirst().orElseThrow();

    assertEquals(1, utilsModule.getSourceFilesList().size()); // MathHelper.ts
    assertTrue(utilsModule.getSubModulesList().isEmpty());

    assertEquals(80, utilsModule.getMetrics().getCoveredInstructions());
    assertEquals(5, utilsModule.getMetrics().getMissedInstructions());
    assertEquals(8, utilsModule.getMetrics().getCoveredBranches());
    assertEquals(2, utilsModule.getMetrics().getMissedBranches());


    // ------------- api-node -------------

    CoverageModule apiModule = rootModule.getSubModulesList().stream()
        .filter(m -> m.getName().equals("api")).findFirst().orElseThrow();

    assertEquals(1, apiModule.getSourceFilesList().size()); // Client.ts
    assertTrue(apiModule.getSubModulesList().isEmpty());

    // API Metrics (Client.ts only)
    assertEquals(60, apiModule.getMetrics().getCoveredInstructions());
    assertEquals(60, apiModule.getMetrics().getMissedInstructions());
    assertEquals(10, apiModule.getMetrics().getCoveredBranches());
    assertEquals(10, apiModule.getMetrics().getMissedBranches());


  }

  @Test
  void testGroupDependentFilesByExtensionWithMixedDepths() {
    CoverageLine line1 = CoverageDashboardManager.measuredLineBuilder()
        .lineNumber(1)
        .coveredInstructions(10).missedInstructions(0)
        .coveredBranches(2).missedBranches(0)
        .build().get();

    CoverageLine line2 = CoverageDashboardManager.measuredLineBuilder()
        .lineNumber(2)
        .coveredInstructions(0).missedInstructions(5)
        .coveredBranches(0).missedBranches(1)
        .build().get();

    // Direct FTL (Level 1 from TS)
    CoverageSourceFile directFtl = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("gui/Direct.ftl")
        .name("Direct.ftl")
        .physicalPath("c:/some/path/gui/Direct.ftl")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(10).missedInstructions(0)
            .coveredBranches(2).missedBranches(0)
            .coveredLines(1).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get())
        .linesAdd(line1)
        .build().get();

    // Nested FTL (Level 2 from TS via Java)
    CoverageSourceFile nestedFtl = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("core/Nested.ftl")
        .name("Nested.ftl")
        .physicalPath("c:/some/path/core/Nested.ftl")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(0).missedInstructions(5)
            .coveredBranches(0).missedBranches(1)
            .coveredLines(0).missedLines(1)
            .partialLines(0).unmappedLines(0)
            .build().get())
        .linesAdd(line2)
        .build().get();

    // Intermediate Java (Level 1 from TS)
    CoverageSourceFile intermediateJava = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("core/Intermediate.java")
        .name("Intermediate.java")
        .physicalPath("c:/some/path/core/Intermediate.java")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(20).missedInstructions(0)
            .coveredBranches(0).missedBranches(0)
            .coveredLines(1).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get())
        .linesAdd(line1)
        .generatedByAdd(nestedFtl)
        .build().get();

    // Root TS files
    CoverageSourceFile tsFileWithDirectFtl = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("DirectView.ts")
        .name("DirectView.ts")
        .physicalPath("c:/some/path/DirectView.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(0).missedInstructions(0)
            .coveredBranches(0).missedBranches(0)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get())
        .linesAdd(line1)
        .generatedByAdd(directFtl)
        .build().get();

    CoverageSourceFile tsFileWithJava = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("NestedView.ts")
        .name("NestedView.ts")
        .physicalPath("c:/some/path/NestedView.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(0).missedInstructions(0)
            .coveredBranches(0).missedBranches(0)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get())
        .linesAdd(line1)
        .generatedByAdd(intermediateJava)
        .build().get();

    CoverageModuleAggregator aggregator = new CoverageModuleAggregator();
    List<CoverageSourceFile> flatFiles = Arrays.asList(tsFileWithDirectFtl, tsFileWithJava);

    // Group FTL files: should find BOTH Direct.ftl (hop 1) and Nested.ftl (hop 2)
    CoverageModule ftlRoot = aggregator.groupDependentFilesByExtension("ftl", flatFiles);
    assertNotNull(ftlRoot);
    assertEquals(2, ftlRoot.getSubModulesList().size()); // gui and core submodules
    assertEquals(10, ftlRoot.getMetrics().getCoveredInstructions());
    assertEquals(5, ftlRoot.getMetrics().getMissedInstructions());

    // Group JAVA files: should find only Intermediate.java (hop 1)
    CoverageModule javaRoot = aggregator.groupDependentFilesByExtension("java", flatFiles);
    assertNotNull(javaRoot);
    assertEquals(1, javaRoot.getSubModulesList().size()); // core submodule
    CoverageModule coreJava = javaRoot.getSubModulesList().get(0);
    assertEquals(1, coreJava.getSourceFilesList().size());
    assertEquals("Intermediate.java", coreJava.getSourceFilesList().get(0).getName());
  }

  @Test
  void testMergeCoverageDataAtLeastOneCoversSemantics() {
    CoverageLine line1Covered = CoverageDashboardManager.measuredLineBuilder()
        .lineNumber(1)
        .coveredInstructions(1).missedInstructions(0)
        .coveredBranches(0).missedBranches(0)
        .build().get();

    CoverageLine line2Missed = CoverageDashboardManager.measuredLineBuilder()
        .lineNumber(2)
        .coveredInstructions(0).missedInstructions(1)
        .coveredBranches(0).missedBranches(0)
        .build().get();

    CoverageLine line1Missed = CoverageDashboardManager.measuredLineBuilder()
        .lineNumber(1)
        .coveredInstructions(0).missedInstructions(1)
        .coveredBranches(0).missedBranches(0)
        .build().get();

    CoverageLine line2Covered = CoverageDashboardManager.measuredLineBuilder()
        .lineNumber(2)
        .coveredInstructions(1).missedInstructions(0)
        .coveredBranches(0).missedBranches(0)
        .build().get();

    // Contributor 1: covers line 1, misses line 2
    CoverageSourceFile javaFileFromTest1 = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("common/Shared.java")
        .name("Shared.java")
        .physicalPath("c:/some/path/common/Shared.java")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(1).missedInstructions(1)
            .coveredBranches(0).missedBranches(0)
            .coveredLines(1).missedLines(1)
            .partialLines(0).unmappedLines(0)
            .build().get())
        .linesAdd(line1Covered)
        .linesAdd(line2Missed)
        .build().get();

    // Contributor 2: misses line 1, covers line 2
    CoverageSourceFile javaFileFromTest2 = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("common/Shared.java")
        .name("Shared.java")
        .physicalPath("c:/some/path/common/Shared.java")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(1).missedInstructions(1)
            .coveredBranches(0).missedBranches(0)
            .coveredLines(1).missedLines(1)
            .partialLines(0).unmappedLines(0)
            .build().get())
        .linesAdd(line1Missed)
        .linesAdd(line2Covered)
        .build().get();

    CoverageSourceFile rootTs1 = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("Test1.ts")
        .name("Test1.ts")
        .physicalPath("c:/some/path/Test1.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(0).missedInstructions(0)
            .coveredBranches(0).missedBranches(0)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get())
        .linesAdd(line1Covered)
        .generatedByAdd(javaFileFromTest1)
        .build().get();

    CoverageSourceFile rootTs2 = CoverageDashboardManager.coverageSourceFileBuilder()
        .category(ArtifactCategory.GENERATED)
        .path("Test2.ts")
        .name("Test2.ts")
        .physicalPath("c:/some/path/Test2.ts")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(0).missedInstructions(0)
            .coveredBranches(0).missedBranches(0)
            .coveredLines(0).missedLines(0)
            .partialLines(0).unmappedLines(0)
            .build().get())
        .linesAdd(line2Covered)
        .generatedByAdd(javaFileFromTest2)
        .build().get();

    CoverageModuleAggregator aggregator = new CoverageModuleAggregator();
    CoverageModule javaRoot = aggregator.groupDependentFilesByExtension("java", Arrays.asList(rootTs1, rootTs2));

    assertNotNull(javaRoot);
    CoverageModule commonModule = javaRoot.getSubModulesList().get(0);
    CoverageSourceFile mergedSharedJava = commonModule.getSourceFilesList().get(0);

    // Both lines should be COVERED because each was covered by at least one test
    assertEquals(2, mergedSharedJava.getMetrics().getCoveredLines());
    assertEquals(0, mergedSharedJava.getMetrics().getMissedLines());
    assertEquals(0, mergedSharedJava.getMetrics().getPartialLines());
  }
}
