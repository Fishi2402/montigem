package coveragedashboard.ingestion;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import coveragedashboard.ArtifactCategory;
import coveragedashboard.ColumnRangeType;
import coveragedashboard.CoverageColumnRange;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageLineBuilder;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.CoverageSourceFileBuilder;
import coveragedashboard.ReportType;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSource;
import de.monticore.sourcemap.DecodedSourceMap;
import de.monticore.sourcemap.Decoding;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


class C8JsonCoverageParser implements CoverageReportParser {

  private static final String BundlePath = File.separator + "dist" + File.separator + "client" + File.separator + "browser" + File.separator;
  private static final String BundledUmlp2JavaPath = File.separator + "frontend" + File.separator + "build" + File.separator + "app" + File.separator + "gui2java2ts" + File.separator;
  private static final String Umlp2JavaPath = File.separator + "frontend" + File.separator + "build" + File.separator + "gui2java2ts" + File.separator;
  private static final String BundledGui2Java2TsPath = File.separator + "frontend" + File.separator + "build" + File.separator + "app" + File.separator + "gui2java2ts" + File.separator;
  private static final String Gui2Java2TsPath = File.separator + "frontend" + File.separator + "build" + File.separator + "gui2java2ts" + File.separator;
  private static final String ComponentObserverManagerRegex = "^.*/frontend/build/app/gui2java2ts/[^/]+ComponentObserverManager\\.ts";
  private static final String ComponentObserverManagerPath = File.separator + "frontend" + File.separator + "build" + File.separator + "umlp2jsweetObserver" + File.separator;


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
        .type(ReportType.GENERATED_CODE_TEST)
        .module(rootModule)
        .build().get();
  }

  private CoverageSourceFile createSourceFile(
      String filePath, Path basePath, Map<String, Range> statementMap,
      Map<String, List<Range>> branchMap, Map<String, Integer> statementHits,
      Map<String, List<Integer>> branchHits) throws IOException{
    Map<Integer, CoverageLineBuilder> lines = new HashMap<>();

    int fileCoveredInstructions = 0;
    int fileMissedInstructions = 0;
    int fileCoveredBranches = 0;
    int fileMissedBranches = 0;
    int fileCoveredLines = 0;
    int fileMissedLines = 0;

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

    for(CoverageLineBuilder lineBuilder : lines.values()){
      // The line counts as covered if any instruction or branch if the line has not any non-covered
      // instructions or branches
      if(lineBuilder.getMissedInstructions() > 0 || lineBuilder.getMissedBranches() > 0){
        fileMissedLines++;
      }else{
        fileCoveredLines++;
      }
    }

    CoverageMetrics fileMetrics = CoverageDashboardManager.coverageMetricsBuilder()
        .coveredInstructions(fileCoveredInstructions)
        .missedInstructions(fileMissedInstructions)
        .coveredBranches(fileCoveredBranches)
        .missedBranches(fileMissedBranches)
        .coveredLines(fileCoveredLines)
        .missedLines(fileMissedLines)
        .build().get();

    CoverageSourceFileBuilder coverageSourceFileBuilder = CoverageDashboardManager.coverageSourceFileBuilder()
        .path(extractRelativePath(filePath, basePath))
        .name(extractFileName(filePath))
        .physicalPath(extractPhysicalPath(filePath))
        .category(ArtifactCategory.GENERATED)
        .metrics(fileMetrics);

    for(CoverageLineBuilder lineBuilder : lines.values()){
      coverageSourceFileBuilder.linesAdd(lineBuilder.build().get());
    }

    // Check if the file was generated
    try {
      List<CoverageSourceFile> coverageSourceFileGeneratedBy = getGenerationFiles(coverageSourceFileBuilder, basePath);
      coverageSourceFileBuilder.addAllGeneratedBys(coverageSourceFileGeneratedBy);
    }catch (URISyntaxException e){
      Log.error("Not able to resolve source-map for " + coverageSourceFileBuilder.getPath() + ": " + e.getMessage());
    }

    return coverageSourceFileBuilder.build().get();
  }


  private List<CoverageSourceFile> getGenerationFiles(CoverageSourceFileBuilder targetFileBuilder, Path basePath) throws IOException, URISyntaxException {
    List<CoverageSourceFile> generatedBy = new ArrayList<>();

    // Check if a source-map exists
    Path sourceMapPath = Path.of(targetFileBuilder.getPhysicalPath() + ".map");
    if(Files.exists(sourceMapPath)){
      String sourceMapContent = Files.readString(sourceMapPath);
      DecodedSourceMap sourceMap = Decoding.parseSourceMapJson(
          sourceMapContent, sourceMapPath.getParent().toUri().toURL());
      List<DecodedMapping> decodedMappings = sourceMap.getMappings();

      // Performance: Pre-map target lines for O(1) lookup
      Map<Integer, CoverageLine> targetLineMap = targetFileBuilder.getLinesList().stream().collect(
          Collectors.toMap(CoverageLine::getLineNumber, l -> l, (existing, replacement) -> existing)
      );

      for(DecodedSource decodedSource : sourceMap.sources){
        // Skip if we can not find the source, or it is explicitly ignored
        if(decodedSource.ignored || decodedSource.url == null)
          continue;

        CoverageSourceFileBuilder sourceCoverageFile = CoverageDashboardManager.coverageSourceFileBuilder()
            .path(extractRelativePath(Path.of(decodedSource.url.toURI()), basePath))
            .name(extractFileName(Path.of(decodedSource.url.toURI())))
            .physicalPath(Path.of(decodedSource.url.toURI()).toString())
            // Check how we will decide this later on!
            .category(ArtifactCategory.GENERATED);

        Map<Integer, CoverageLineBuilder> lineBuilders = new HashMap<Integer, CoverageLineBuilder>();
        // Get all mappings for this source and build the lines based on the coverage data
        decodedMappings.stream().filter(mapping -> mapping.originalSource != null && mapping.originalSource.url.sameFile(decodedSource.url))
            .forEach(mapping ->{

              CoverageLine targetLine = targetLineMap.get(mapping.generatedLine);
              if(targetLine == null)
                return;

              // Get the line-coverage for this line in the target file and based on this, we set the coverage for the source-file
              CoverageLineBuilder line = lineBuilders.computeIfAbsent(mapping.originalLine,
                    (lineNumber) -> CoverageDashboardManager.coverageLineBuilder()
                        .lineNumber(lineNumber)
                        .coveredInstructions(0)
                        .missedInstructions(0)
                        .coveredBranches(0)
                        .missedBranches(0));

              // For now, only map the lines back
              line.missedInstructions(line.getMissedInstructions() + targetLine.getMissedInstructions());
              line.coveredInstructions(line.getCoveredInstructions() + targetLine.getCoveredInstructions());
              line.missedBranches(line.getMissedBranches() + targetLine.getMissedBranches());
              line.coveredBranches(line.getCoveredBranches() + targetLine.getCoveredBranches());
        });

        int fileCoveredInstructions = 0;
        int fileMissedInstructions = 0;
        int fileCoveredBranches = 0;
        int fileMissedBranches = 0;
        int fileCoveredLines = 0;
        int fileMissedLines = 0;

        // Finish the builder and add to the source-file and build up the file-metrics on the fly
        for(CoverageLineBuilder lineBuilder : lineBuilders.values()){
          sourceCoverageFile.linesAdd(lineBuilder.build().get());
          fileCoveredBranches += lineBuilder.getCoveredBranches();
          fileCoveredInstructions += lineBuilder.getCoveredInstructions();
          fileMissedBranches += lineBuilder.getMissedBranches();
          fileMissedInstructions += lineBuilder.getMissedInstructions();
          if(lineBuilder.getMissedInstructions() > 0 || lineBuilder.getMissedBranches() > 0){
            fileMissedLines++;
          }else{
            fileCoveredLines++;
          }
        }

        CoverageMetrics fileMetrics = CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(fileCoveredInstructions)
            .missedInstructions(fileMissedInstructions)
            .coveredBranches(fileCoveredBranches)
            .missedBranches(fileMissedBranches)
            .coveredLines(fileCoveredLines)
            .missedLines(fileMissedLines)
            .build().get();

        sourceCoverageFile.metrics(fileMetrics);

        generatedBy.add(sourceCoverageFile.build().get());
      }
    }

    return generatedBy;
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
    return extractFileName(Path.of(fullPath));
  }

  private String extractFileName(Path fullPath){
    Path fileName = fullPath.normalize().getFileName();
    return fileName != null ? fileName.toString() : "unknown";
  }

  private String extractRelativePath(String fullPath, Path basePath){
    if(fullPath == null || fullPath.isBlank())
      return "unknown";
    return extractRelativePath(Path.of(fullPath), basePath);
  }

  private String extractRelativePath(Path fullPath, Path basePath){
    Path target = fullPath.normalize();
    if(basePath == null || !target.startsWith(basePath))
      return fullPath.toString();
    return basePath.relativize(target).toString();
  }

  private String extractPhysicalPath(String fullPath){
    // Remove the 'dist/client/browser' from the path as this is done during bundling
    String physicalPath = fullPath.replace(BundlePath, File.separator);

    // Check if this is the 'xxComponentObserverManager.ts'

    if(physicalPath.replace(File.separator, "/").matches(ComponentObserverManagerRegex)){
      return physicalPath
          .replace(BundledGui2Java2TsPath, ComponentObserverManagerPath);
    }

    // If this is a file generated by 'umlpJava2ts' or 'gui2java2ts', use the respective
    // build-output file as physical file and not the file copied to the frontend-app in order
    // to preserve the relative paths for source-map files
    return physicalPath
        .replace(BundledGui2Java2TsPath, Gui2Java2TsPath)
        .replace(BundledUmlp2JavaPath, Umlp2JavaPath);
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



