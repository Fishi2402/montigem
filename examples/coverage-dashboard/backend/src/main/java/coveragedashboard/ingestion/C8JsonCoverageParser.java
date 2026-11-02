package coveragedashboard.ingestion;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import coveragedashboard.ArtifactCategory;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.ReportType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


class C8JsonCoverageParser implements CoverageReportParser {

  private final JsonFactory jsonFactory;
  private final CoverageModuleAggregator moduleAggregator;

  public C8JsonCoverageParser(){
    this.jsonFactory = new JsonFactory();
    this.moduleAggregator = new CoverageModuleAggregator();
  }

  @Override
  public CoverageReport parse(File jsonFile, String reportName, Path basePath) throws IOException {
    List<CoverageSourceFile> parsedFiles = new ArrayList<>();

    try (JsonParser parser = jsonFactory.createParser(jsonFile)) {

      // Root must be an obejct
      if(parser.nextToken() != JsonToken.START_OBJECT){
        throw new IOException("Expected JSON Object root");
      }

      while(parser.nextToken() != JsonToken.END_OBJECT){
        // Read current file path and move to inner object
        String currentKey = parser.currentName();
        parser.nextToken();

        String filePath = null;
        int fileCoveredInstructions = 0;
        int fileMissedInstructions = 0;
        int fileCoveredBranches = 0;
        int fileMissedBranches = 0;

        while(parser.nextToken() != JsonToken.END_OBJECT){
          String fieldName = parser.currentName();
          parser.nextToken();
          if("path".equals(fieldName)){
            filePath = parser.getText();
          }else if("s".equals(fieldName)){
            // "s" object has the following structure: {"0":1,"1":1,"2":1,"3":1,"4":1,"5":1,"6":1, ...}
            while(parser.nextToken() != JsonToken.END_OBJECT){
              parser.nextToken();
              int hits = parser.getIntValue();
              if(hits > 0)
                fileCoveredInstructions++;
              else
                fileMissedInstructions++;
            }
          }else if("b".equals(fieldName)){
            // "b" has the following structure: {"0": [1,0], "1": [3], ...}
            while(parser.nextToken() != JsonToken.END_OBJECT){
              parser.nextToken();
              while(parser.nextToken() != JsonToken.END_ARRAY){
                int hits = parser.getIntValue();
                if(hits > 0)
                  fileCoveredBranches++;
                else
                  fileMissedBranches++;
              }
            }
          }else{
            // Not interesting for now in first version (e.g. fnMap, statementMap, ...)
            parser.skipChildren();
          }
        }

        if(filePath != null){
          CoverageMetrics fileMetrics = CoverageDashboardManager.coverageMetricsBuilder()
              .coveredInstructions(fileCoveredInstructions)
              .missedInstructions(fileMissedInstructions)
              .coveredBranches(fileCoveredBranches)
              .missedBranches(fileMissedBranches)
              .coveredLines(0).missedLines(0)
              .build().get();

          // Add dummy line data for now
          CoverageLine dummyLine = CoverageDashboardManager.coverageLineBuilder()
              .lineNumber(1)
              .coveredInstructions(0).missedInstructions(0)
              .coveredBranches(0).missedBranches(0)
              .build().get();

          CoverageSourceFile sourceFile = CoverageDashboardManager.coverageSourceFileBuilder()
              .path(extractRelativePath(filePath, basePath))
              .name(extractFileName(filePath))
              .category(ArtifactCategory.GENERATED)
              .metrics(fileMetrics)
              .linesAdd(dummyLine)
              .build().get();

          parsedFiles.add(sourceFile);
        }
      }
    }

    // Aggregate into modules
    CoverageModule rootModule = moduleAggregator.groupFilesInModules(parsedFiles);

    return CoverageDashboardManager.coverageReportBuilder()
        .name(reportName)
        .type(ReportType.E2E_FRONTEND)
        .module(rootModule)
        .build().get();
  }

  private String extractFileName(String fullPath){
    if(fullPath == null || fullPath.isBlank())
      return "unknown";
    Path path = Paths.get(fullPath).normalize();
    Path fileName = path.getFileName();
    return fileName != null ? fileName.toString() : "unknown";
  }

  private String extractRelativePath(String fullPath, Path basePath){
    if(fullPath == null || fullPath.isBlank())
      return "unknown";
    return Paths.get(fullPath).normalize().relativize(basePath).toString();
  }

}



