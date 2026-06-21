package coveragedashboard.ingestion;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import coveragedashboard.ArtifactCategory;
import coveragedashboard.ColumnRangeType;
import coveragedashboard.CoverageColumnRange;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLineBuilder;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.CoverageSourceFileBuilder;
import coveragedashboard.ReportType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        String filePath = parser.currentName();
        parser.nextToken();

        Map<String, Range> statementMap = new HashMap<>();
        Map<String, List<Range>> branchMap = new HashMap<>();
        Map<String, Integer> statementHits = new HashMap<>();
        Map<String, List<Integer>> branchHits = new HashMap<>();



        while(parser.nextToken() != JsonToken.END_OBJECT){
          String fieldName = parser.currentName();
          JsonToken token = parser.nextToken();

          if ("statementMap".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseStatementMap(parser, statementMap);
          } else if ("branchMap".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseBranchMap(parser, branchMap);
          } else if ("s".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseStatementHits(parser, statementHits);
          } else if ("b".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseBranchHits(parser, branchHits);
          } else {
            parser.skipChildren(); // Ignore fnMap, f, etc. for now
          }
        }



        if(filePath != null){
          parsedFiles.add(createSourceFile(filePath, basePath, statementMap, branchMap, statementHits, branchHits));
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

  private CoverageSourceFile createSourceFile(
      String filePath, Path basePath, Map<String, Range> statementMap,
      Map<String, List<Range>> branchMap, Map<String, Integer> statementHits,
      Map<String, List<Integer>> branchHits){
    Map<Integer, CoverageLineBuilder> lines = new HashMap<>();

    int fileCoveredInstructions = 0;
    int fileMissedInstructions = 0;
    int fileCoveredBranches = 0;
    int fileMissedBranches = 0;

    // Statements
    for(Map.Entry<String, Range> entry : statementMap.entrySet()){
      String id = entry.getKey();
      Range range = entry.getValue();
      int hits = statementHits.getOrDefault(id, 0);

      CoverageLineBuilder lineBuilder = lines.computeIfAbsent(range.startLine,(line) -> CoverageDashboardManager.coverageLineBuilder()
          .lineNumber(line)
          .coveredInstructions(0)
          .missedInstructions(0)
          .coveredBranches(0)
          .missedBranches(0));
      lineBuilder.columnRangesAdd(createColumnRange(range, hits, ColumnRangeType.STATEMENT));

      if(hits > 0){
        lineBuilder.coveredInstructions(lineBuilder.getCoveredInstructions()  + 1);
        fileCoveredInstructions++;
      }else{
        lineBuilder.missedInstructions(lineBuilder.getMissedInstructions() + 1);
        fileMissedInstructions++;
      }
    }

    // Branches
    for(Map.Entry<String, List<Range>> entry : branchMap.entrySet()){
      String id = entry.getKey();
      List<Range> locations = entry.getValue();
      List<Integer> hitsList = branchHits.getOrDefault(id, List.of(0));

      for(int i = 0; i < locations.size(); i++){
        Range range = locations.get(i);
        int hits = (hitsList != null && i < hitsList.size()) ? hitsList.get(i) : 0;
        CoverageLineBuilder lineBuilder = lines.computeIfAbsent(range.startLine,(line) -> CoverageDashboardManager.coverageLineBuilder()
            .lineNumber(line)
            .coveredInstructions(0)
            .missedInstructions(0)
            .coveredBranches(0)
            .missedBranches(0)
        );
        lineBuilder.columnRangesAdd(createColumnRange(range, hits, ColumnRangeType.BRANCH));

        if(hits > 0){
          lineBuilder.coveredBranches(lineBuilder.getCoveredBranches() + 1);
          fileCoveredBranches++;
        }else{
          lineBuilder.missedBranches(lineBuilder.getMissedBranches() + 1);
          fileMissedBranches++;
        }
      }
    }

    CoverageMetrics fileMetrics = CoverageDashboardManager.coverageMetricsBuilder()
        .coveredInstructions(fileCoveredInstructions)
        .missedInstructions(fileMissedInstructions)
        .coveredBranches(fileCoveredBranches)
        .missedBranches(fileMissedBranches)
        .coveredLines(0).missedLines(0) // Can be calculated based on lines map size if needed
        .build().get();

    CoverageSourceFileBuilder coverageSourceFileBuilder = CoverageDashboardManager.coverageSourceFileBuilder()
        .path(extractRelativePath(filePath, basePath))
        .name(extractFileName(filePath))
        .category(ArtifactCategory.GENERATED)
        .metrics(fileMetrics);

    for(CoverageLineBuilder lineBuilder : lines.values()){
      coverageSourceFileBuilder.linesAdd(lineBuilder.build().get());
    }

    return coverageSourceFileBuilder.build().get();
  }

  private CoverageColumnRange createColumnRange(Range range, int hits, ColumnRangeType type){
    return CoverageDashboardManager.coverageColumnRangeBuilder()
        .fromColumn(range.startColumn)
        .toColumn(range.endColumn)
        .hits(hits)
        .type(type)
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
    Path target = Paths.get(fullPath).normalize();
    if(basePath == null || !target.startsWith(basePath))
      return fullPath;
    return basePath.relativize(target).toString();
  }


  private void parseStatementMap(JsonParser parser,Map<String, Range> map) throws IOException{
    while(parser.nextToken() != JsonToken.END_OBJECT){
      String statement  = parser.currentName();
      parser.nextToken();
      map.put(statement, parseRange(parser));
    }
  }

  private void parseBranchMap(JsonParser parser, Map<String, List<Range>> map) throws IOException {
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String branch = parser.currentName();
      parser.nextToken();

      List<Range> locations = new ArrayList<>();
      while (parser.nextToken() != JsonToken.END_OBJECT) {
        String name = parser.currentName();
        JsonToken token = parser.nextToken();
        if ("locations".equals(name) && token == JsonToken.START_ARRAY) {
          while (parser.nextToken() != JsonToken.END_ARRAY) {
            locations.add(parseRange(parser));
          }
        } else {
          parser.skipChildren();
        }
      }
      map.put(branch, locations);
    }
  }

  private void parseStatementHits(JsonParser parser, Map<String, Integer> map) throws IOException {
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String id = parser.currentName();
      parser.nextToken();
      map.put(id, parser.getIntValue());
    }
  }

  private void parseBranchHits(JsonParser parser, Map<String, List<Integer>> map) throws IOException {
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String id = parser.currentName();
      parser.nextToken(); // Move to START_ARRAY

      List<Integer> hits = new ArrayList<>();
      while (parser.nextToken() != JsonToken.END_ARRAY) {
        hits.add(parser.getIntValue());
      }
      map.put(id, hits);
    }
  }

  private Range parseRange(JsonParser parser) throws IOException{
    Range range = new Range();
    while(parser.nextToken() != JsonToken.END_OBJECT){
      String name = parser.currentName();
      JsonToken token = parser.nextToken();
      if ("start".equals(name) && token == JsonToken.START_OBJECT) {
        parseLocationValues(parser, range, true);
      } else if ("end".equals(name) && token == JsonToken.START_OBJECT) {
        parseLocationValues(parser, range, false);
      } else {
        parser.skipChildren();
      }
    }
    return range;
  }

  private void parseLocationValues(JsonParser parser, Range range, boolean isStart) throws IOException{
    while(parser.nextToken() != JsonToken.END_OBJECT){
      String name = parser.currentName();
      parser.nextToken();
      if("line".equals(name)){
        if(isStart)
          range.startLine = parser.getIntValue();
        else
          range.endLine = parser.getIntValue();
      }else if("column".equals(name)){
        if(isStart)
          range.startColumn = parser.getIntValue();
        else
          range.endColumn = parser.getIntValue();
      }else{
        parser.skipChildren();
      }
    }
  }

  private static class Range{
    int startLine;
    int startColumn;
    int endLine;
    int endColumn;
  }

}



