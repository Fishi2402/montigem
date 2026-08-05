package coveragedashboard.ingestion;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import coveragedashboard.ArtifactCategory;
import coveragedashboard.ColumnRangeType;
import coveragedashboard.CoverageColumnRangeBuilder;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageLineBuilder;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageScope;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.CoverageSourceFileBuilder;
import coveragedashboard.ReportType;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSource;
import de.monticore.sourcemap.DecodedSourceMap;
import de.monticore.sourcemap.Decoding;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class C8JsonCoverageParser implements CoverageReportParser {

  private static final String BundlePath = "/dist/client/browser/";
  private static final String BundledUmlp2JavaPath = "/frontend/build/app/umlpJava2ts/";
  private static final String Umlp2JavaPath = "/frontend/build/umlpJava2ts/";
  private static final String BundledGui2Java2TsPath = "/frontend/build/app/gui2java2ts/";
  private static final String Gui2Java2TsPath = "/frontend/build/gui2java2ts/";
  private static final String ComponentObserverManagerRegex = "^.*/frontend/build/app/gui2java2ts/[^/]+ComponentObserverManager\\.ts";
  private static final String ComponentObserverManagerPath = "/frontend/build/umlp2jsweetObserver/";


  private final JsonFactory jsonFactory;
  private final CoverageModuleAggregator moduleAggregator;
  private final List<ResourcePath> resourcePaths;

  public C8JsonCoverageParser(){
    this.jsonFactory = new JsonFactory();
    this.moduleAggregator = new CoverageModuleAggregator();
    this.resourcePaths = new ArrayList<>();
  }

  @Override
  public CoverageReport parse(File jsonFile, String reportName, Path basePath, Path rootPath) throws IOException {
    for(ResourcePath resourcePath : resourcePaths){
      resourcePath.close();
    }
    resourcePaths.clear();

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
        Map<String, BranchData> branchMap = new HashMap<>();
        Map<String, Integer> statementHits = new HashMap<>();
        Map<String, List<Integer>> branchHits = new HashMap<>();
        Map<String, FnData> fnMap = new HashMap<>();
        Map<String, Integer> fnHits = new HashMap<>();

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
          } else if("fnMap".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseFnMap(parser, fnMap);
          }else if("f".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseFnHits(parser, fnHits);
          } else {
            parser.skipChildren(); // Skip unknown properties for now
          }
        }

        if(filePath != null){
          parsedFiles.add(createSourceFile(
              filePath, basePath, rootPath,
              statementMap, branchMap,
              statementHits, branchHits, fnMap, fnHits));
        }
      }
    }

    for(ResourcePath resourcePath : resourcePaths){
      resourcePath.close();
    }
    resourcePaths.clear();

    // Aggregate into modules
    CoverageModule rootModule = moduleAggregator.groupFilesInModules(parsedFiles);

    return CoverageDashboardManager.coverageReportBuilder()
        .name(reportName)
        .type(ReportType.GENERATED_CODE_TEST)
        .module(rootModule)
        .build().get();
  }

  private CoverageSourceFile createSourceFile(
      String filePath, Path basePath, Path rootPath, Map<String, Range> statementMap,
      Map<String, BranchData> branchMap, Map<String, Integer> statementHits,
      Map<String, List<Integer>> branchHits, Map<String, FnData> fnMap, Map<String, Integer> fnHits) throws IOException{

    FileCoverageContext context = new FileCoverageContext();
    extractFunctionScopes(fnMap, fnHits, context);
    processStatements(statementMap, statementHits, context);
    processBranches(branchMap, branchHits, context);

    finalizeLinesAndDeduplicate(context);

    CoverageSourceFileBuilder fileBuilder = CoverageDashboardManager.coverageSourceFileBuilder()
        .path(extractRelativePath(filePath, basePath))
        .name(extractFileName(filePath))
        .physicalPath(extractPhysicalPath(filePath, rootPath))
        .category(ArtifactCategory.GENERATED)
        .scopes(context.scopes)
        .metrics(context.metrics.build());

    context.lines.values().forEach(line -> fileBuilder.linesAdd(line.build().get()));

    try{
      fileBuilder.addAllGeneratedBys(getGenerationFiles(fileBuilder, basePath));
    }catch (URISyntaxException e){
      Log.error("Not able to resolve source-map for " + fileBuilder.getPath() + ": " + e.getMessage());
    }

    return fileBuilder.build().get();
  }


  private List<CoverageSourceFile> getGenerationFiles(CoverageSourceFileBuilder targetFileBuilder, Path basePath) throws IOException, URISyntaxException {
    List<CoverageSourceFile> generatedBy = new ArrayList<>();

    // Check if a source-map exists
    Path sourceMapPath;
    try {
      sourceMapPath = Path.of(targetFileBuilder.getPhysicalPath() + ".map");
      if (!Files.exists(sourceMapPath)) {
        // Check if template-source maps exist
        sourceMapPath = Path.of(FilenameUtils.removeExtension(targetFileBuilder.getPhysicalPath()) + "_TEMPL.map");
      }
      if (!Files.exists(sourceMapPath)) {
        return generatedBy;
      }
    } catch (Exception e) {
      Log.error("Not able to check for source maps: " + e.getMessage());
      return generatedBy;
    }

    String sourceMapContent = Files.readString(sourceMapPath);
    DecodedSourceMap sourceMap;
    try {
      sourceMap = Decoding.parseSourceMapJson(
          sourceMapContent, sourceMapPath.getParent().toUri().toURL());
    }catch (Exception e){
      Log.error("Error while parsing source-map for " + targetFileBuilder.getPath() + ": " + e.getMessage());
      return generatedBy;
    }
    List<DecodedMapping> decodedMappings = sourceMap.getMappings();

    // Performance: Pre-map target lines for O(1) lookup
    Map<Integer, CoverageLine> targetLineMap = targetFileBuilder.getLinesList().stream().collect(
        Collectors.toMap(CoverageLine::getLineNumber, l -> l, (existing, replacement) -> existing)
    );

    for(DecodedSource decodedSource : sourceMap.sources){
      // Skip if we can not find the source, or it is explicitly ignored
      if(decodedSource.ignored || decodedSource.url == null)
        continue;
      ResourcePath resourcePath = ResourcePath.of(URI.create(decodedSource.url.toString().replace('\\', '/')));
      Path decodedSourcePath = resourcePath.getPath().normalize();
      this.resourcePaths.add(resourcePath);

      CoverageSourceFileBuilder sourceCoverageFile = CoverageDashboardManager.coverageSourceFileBuilder()
          .path(extractRelativePath(decodedSourcePath, basePath))
          .name(extractFileName(decodedSourcePath))
          .physicalPath(resourcePath.representation)
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

      // Skip if no line was added, no need to have empty files stored
      if(sourceCoverageFile.getLinesList().isEmpty()){
        continue;
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

      // Check if this file is generated as well
      try{
        sourceCoverageFile.addAllGeneratedBys(this.getGenerationFiles(sourceCoverageFile, basePath));
      }catch (URISyntaxException e) {
        Log.error("Not able to resolve source-map for " + sourceCoverageFile.getPath() + ": " + e.getMessage());
      }

      generatedBy.add(sourceCoverageFile.build().get());
    }


    return generatedBy;
  }

  private CoverageColumnRangeBuilder createColumnRange(Range range, int hits, ColumnRangeType type){
    return CoverageDashboardManager.coverageColumnRangeBuilder()
        .fromColumn(range.startColumn)
        .toColumn(range.endColumn)
        .hits(hits)
        .type(type);
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

  private String extractRelativePath(String filePath, Path basePath){
    if(filePath == null || filePath.isBlank())
      return "unknown";
    return extractRelativePath(Path.of(filePath), basePath);
  }

  private String extractRelativePath(Path filePath, Path basePath){
    if(basePath == null || !filePath.startsWith(basePath))
      return filePath.toString();
    return basePath.relativize(filePath).toString();
  }

  private String extractPhysicalPath(String filePath, Path rootPath){

    // Resolve relative path to
    String fullPath = rootPath.resolve(Path.of(filePath)).toAbsolutePath().normalize().toString().replace(File.separatorChar, '/');

    // Remove the 'dist/client/browser' from the path as this is done during bundling
    String physicalPath = fullPath.replace(BundlePath, "/");

    // Check if this is the 'xxComponentObserverManager.ts'

    if(physicalPath.matches(ComponentObserverManagerRegex)){
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
      String statement = parser.currentName();
      parser.nextToken();
      map.put(statement, parseRange(parser));
    }
  }

  private void parseBranchMap(JsonParser parser, Map<String, BranchData> map) throws IOException {
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String branch = parser.currentName();
      parser.nextToken();

      BranchData branchData = new BranchData();
      while (parser.nextToken() != JsonToken.END_OBJECT) {
        String name = parser.currentName();
        JsonToken token = parser.nextToken();
        if("loc".equals(name))
          branchData.loc = parseRange(parser);
        else if ("locations".equals(name) && token == JsonToken.START_ARRAY) {
          while (parser.nextToken() != JsonToken.END_ARRAY) {
            branchData.locations.add(parseRange(parser));
          }
        } else {
          parser.skipChildren();
        }
      }
      map.put(branch, branchData);
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

  private void parseFnMap(JsonParser parser, Map<String, FnData> map) throws IOException{
    while(parser.nextToken() != JsonToken.END_OBJECT){
      String id = parser.currentName();
      parser.nextToken();
      FnData fnData = new FnData();
      while(parser.nextToken() != JsonToken.END_OBJECT){
        String fieldName = parser.currentName();
        JsonToken token = parser.nextToken();
        if("name".equals(fieldName)){
          fnData.name = parser.getText();
        }else if("decl".equals(fieldName) && token == JsonToken.START_OBJECT){
          fnData.decl = parseRange(parser);
        }else{
          parser.skipChildren();
        }
      }
      map.put(id, fnData);
    }
  }

  private void parseFnHits(JsonParser parser, Map<String, Integer> map) throws IOException{
    while(parser.nextToken() != JsonToken.END_OBJECT){
      String id = parser.currentName();
      parser.nextToken();
      map.put(id, parser.getIntValue());
    }
  }

  private void deduplicateRanges(CoverageLineBuilder lineBuilder, List<CoverageColumnRangeBuilder> rawRanges) {
    if (rawRanges.isEmpty()) return;

    List<CoverageColumnRangeBuilder> baseRanges = new ArrayList<>();
    List<CoverageColumnRangeBuilder> overlayRanges = new ArrayList<>();

    List<CoverageColumnRangeBuilder> missedBranches = rawRanges.stream()
        .filter(r -> r.getType() == ColumnRangeType.BRANCH && r.getHits() == 0)
        .collect(Collectors.toList());

    for (CoverageColumnRangeBuilder r : rawRanges) {
      if (r.getType() == ColumnRangeType.BRANCH) {
        overlayRanges.add(r);
      } else if(r.getType() == ColumnRangeType.STATEMENT) {
        if(r.getHits() > 0){
          boolean overlapsMissedBranch = missedBranches.stream().anyMatch(mb -> mb.getFromColumn() < r.getToColumn()
              && mb.getToColumn() > r.getFromColumn());
          if(overlapsMissedBranch)
            r.type(ColumnRangeType.PARTIAL);
        }
        baseRanges.add(r);
      } else {
        baseRanges.add(r);
      }
    }

    flattenLayer(lineBuilder, baseRanges, true);
    flattenLayer(lineBuilder, overlayRanges, false);
  }

  private void flattenLayer(CoverageLineBuilder lineBuilder, List<CoverageColumnRangeBuilder> ranges, boolean isBaseLayer) {
    if (ranges.isEmpty()) return;

    List<Integer> endpoints = ranges.stream()
        .flatMap(r -> Stream.of(r.getFromColumn(), r.getToColumn()))
        .distinct()
        .sorted()
        .collect(Collectors.toList());

    CoverageColumnRangeBuilder currentSegment = null;
    SegmentState currentState = null;

    for (int i = 0; i < endpoints.size() - 1; i++) {
      int startCol = endpoints.get(i);
      int endCol = endpoints.get(i + 1);

      SegmentState state = isBaseLayer ? evaluateBaseSegment(startCol, endCol, ranges)
          : evaluateOverlaySegment(startCol, endCol, ranges);

      if (!state.isActive) continue;

      if (currentSegment != null && state.equals(currentState)) {
        currentSegment.toColumn(endCol); // Merge contiguous segments
      } else {
        if (currentSegment != null) lineBuilder.columnRangesAdd(currentSegment.build().get());
        currentSegment = CoverageDashboardManager.coverageColumnRangeBuilder()
            .fromColumn(startCol).toColumn(endCol)
            .hits(state.hits).type(state.type);
        currentState = state;
      }
    }
    if (currentSegment != null) lineBuilder.columnRangesAdd(currentSegment.build().get());
  }

  private SegmentState evaluateBaseSegment(int startCol, int endCol, List<CoverageColumnRangeBuilder> ranges) {
    boolean missedStatement = false, coveredStatement = false, isPartialBlock = false;
    boolean isActive = false;

    for (CoverageColumnRangeBuilder r : ranges) {
      if (r.getFromColumn() <= startCol && r.getToColumn() >= endCol) {
        isActive = true;
        if (r.getType() == ColumnRangeType.PARTIAL) {
          isPartialBlock = true;
        } else if (r.getType() == ColumnRangeType.STATEMENT) {
          if (r.getHits() == 0) missedStatement = true;
          else coveredStatement = true;
        }
      }
    }

    if (!isActive) return new SegmentState(false, 0, null);

    // Partial block wins base layer hierarchy
    if (isPartialBlock) return new SegmentState(true, 1, ColumnRangeType.PARTIAL);
    if (missedStatement) return new SegmentState(true, 0, ColumnRangeType.STATEMENT);
    return new SegmentState(true, 1, ColumnRangeType.STATEMENT);
  }

  private SegmentState evaluateOverlaySegment(int startCol, int endCol, List<CoverageColumnRangeBuilder> ranges) {
    boolean missedBranch = false;
    boolean isActive = false;

    for (CoverageColumnRangeBuilder r : ranges) {
      if (r.getFromColumn() <= startCol && r.getToColumn() >= endCol) {
        isActive = true;
        if (r.getHits() == 0) missedBranch = true;
      }
    }

    if (!isActive) return new SegmentState(false, 0, null);
    // If any overlapping branch condition is missed, highlight the segment as missed-light
    if (missedBranch) return new SegmentState(true, 0, ColumnRangeType.BRANCH);
    return new SegmentState(true, 1, ColumnRangeType.BRANCH);
  }

 private void distributeRange(Range range, int hits, ColumnRangeType type, FileCoverageContext context){
    if(range.startLine == range.endLine) {
      applyToLine(range.startLine, range.startColumn, range.endColumn, hits, type, context);
      return;
    }
    applyToLine(range.startLine, range.startColumn, Integer.MAX_VALUE, hits, type, context);
    for(int line = range.startLine + 1; line < range.endLine; line++){
      applyToLine(line, 0, Integer.MAX_VALUE, hits, type, context);
    }
    applyToLine(range.endLine, 0, range.endColumn, hits, type, context);
 }

 private void applyToLine(
     int lineNumber,
     int startCol,
     int endCol,
     int hits,
     ColumnRangeType type,
     FileCoverageContext context){
    CoverageLineBuilder lineBuilder = context.getLineBuilder(lineNumber);
    context.addRawRange(lineNumber, startCol, endCol, hits, type);
    if(type == ColumnRangeType.STATEMENT){
      if(hits > 0)
        lineBuilder.coveredInstructions(lineBuilder.getCoveredInstructions() + 1);
      else
        lineBuilder.missedInstructions(lineBuilder.getMissedInstructions() + 1);
    }else{
      if(hits > 0)
        lineBuilder.coveredBranches(lineBuilder.getCoveredBranches() + 1);
      else
        lineBuilder.missedBranches(lineBuilder.getMissedBranches() + 1);
    }
  }

  private void finalizeLinesAndDeduplicate(FileCoverageContext context){
    for(Map.Entry<Integer, CoverageLineBuilder> entry : context.lines.entrySet()){
      int lineNumber = entry.getKey();
      CoverageLineBuilder lineBuilder = entry.getValue();
      List<CoverageColumnRangeBuilder> rawRanges = context.rawRangesPerLine.getOrDefault(lineNumber, List.of());
      deduplicateRanges(lineBuilder, rawRanges);
      context.metrics.addLine(lineBuilder);
    }
  }

  private void extractFunctionScopes(Map<String, FnData> fnMap, Map<String, Integer> fnHits, FileCoverageContext context){
    for(Map.Entry<String, FnData> entry : fnMap.entrySet()){
      FnData fnData = entry.getValue();
      if(fnData.decl == null)
        continue;

      int hits = fnHits.getOrDefault(entry.getKey(), 0);
      if(hits == 0)
        context.uncoveredDeclarations.add(fnData.decl);

      CoverageMetrics scopeMetrics = CoverageDashboardManager.coverageMetricsBuilder()
          .coveredInstructions(0)
          .missedInstructions(0)
          .coveredBranches(0)
          .missedBranches(0)
          .coveredLines(0)
          .missedLines(0)
          .build().get();
      context.scopes.add(CoverageDashboardManager.coverageScopeBuilder()
          .name(fnData.name != null ? fnData.name : "anonymous")
          .startLine(fnData.decl.startLine).startColumn(fnData.decl.startColumn)
          .endLine(fnData.decl.endLine).endColumn(fnData.decl.endColumn)
          .metrics(scopeMetrics)
          .build().get()
      );
    }
  }

  private void processStatements(Map<String, Range> statementMap, Map<String, Integer> statementHits, FileCoverageContext context){
    for(Map.Entry<String, Range> entry: statementMap.entrySet()){
      Range range = entry.getValue();
      int hits = statementHits.getOrDefault(entry.getKey(), 0);
      if(hits > 0 && isInsideUncoveredDeclaration(range, context.uncoveredDeclarations))
        hits = 0; // Dismiss hit if triggered only during class import signature

      context.metrics.addInstruction(hits);
      distributeRange(range, hits, ColumnRangeType.STATEMENT, context);
    }
  }

  private void processBranches(Map<String, BranchData> branchMap, Map<String, List<Integer>> branchHits, FileCoverageContext context){
    for(Map.Entry<String, BranchData> entry: branchMap.entrySet()){
      String id = entry.getKey();
      BranchData data = entry.getValue();
      List<Integer> hitsList = branchHits.getOrDefault(entry.getKey(), List.of(0));

      boolean hasHits = false;
      boolean hasMiss = false;

      for(int i = 0; i < data.locations.size(); i++){
        Range range = data.locations.get(i);
        int hits = i < hitsList.size() ? hitsList.get(i) : 0;

        if(hits > 0)
          hasHits = true;
        else
          hasMiss = true;

        context.metrics.addBranch(hits);
        distributeRange(range, hits, ColumnRangeType.BRANCH, context);
      }

      if(hasHits && hasMiss && data.loc != null){
        distributeRange(data.loc, 0, ColumnRangeType.PARTIAL, context);
      }
    }
  }

  private boolean isInsideUncoveredDeclaration(Range range, List<Range> uncoveredDeclarations){
    return uncoveredDeclarations.stream().anyMatch(decl -> range.startLine >= decl.startLine
        && range.endLine <= decl.endLine);
  }

  private static class Range{
    int startLine;
    int startColumn;
    int endLine;
    int endColumn;
  }

  private static class FnData{
    String name;
    Range decl;
  }

  private static class FileCoverageContext{
    final Map<Integer, CoverageLineBuilder> lines = new HashMap<>();
    final Map<Integer, List<CoverageColumnRangeBuilder>> rawRangesPerLine = new HashMap<>();
    final List<Range> uncoveredDeclarations = new ArrayList<>();
    final Set<CoverageScope> scopes = new HashSet<>();
    final CoverageMetricsAccumulator metrics = new CoverageMetricsAccumulator();

    CoverageLineBuilder getLineBuilder(int lineNumber){
      return lines.computeIfAbsent(lineNumber, l -> CoverageDashboardManager.coverageLineBuilder()
          .lineNumber(l)
          .coveredInstructions(0)
          .missedInstructions(0)
          .coveredBranches(0)
          .missedBranches(0)
      );
    }

    void addRawRange(int lineNumber, int startCol, int endCol, int hits, ColumnRangeType type){
      rawRangesPerLine.computeIfAbsent(lineNumber, l -> new ArrayList<>())
          .add(CoverageDashboardManager.coverageColumnRangeBuilder()
              .fromColumn(startCol)
              .toColumn(endCol)
              .hits(hits)
              .type(type));
    }
  }

  private static class CoverageMetricsAccumulator{
    int coveredInstructions;
    int missedInstructions;
    int coveredBranches;
    int missedBranches;
    int coveredLines;
    int missedLines;

    void addInstruction(int hits){
      if(hits > 0)
        coveredInstructions++;
      else
        missedInstructions++;
    }

    void addBranch(int hits){
      if(hits > 0)
        coveredBranches++;
      else
        missedBranches++;
    }

    void addLine(CoverageLineBuilder lineBuilder){
      if(lineBuilder.getMissedInstructions() > 0 || lineBuilder.getMissedBranches() > 0)
        missedLines++;
      else
        coveredLines++;
    }

    CoverageMetrics build(){
      return CoverageDashboardManager.coverageMetricsBuilder()
          .coveredInstructions(coveredInstructions)
          .missedInstructions(missedInstructions)
          .coveredBranches(coveredBranches)
          .missedBranches(missedBranches)
          .coveredLines(coveredLines)
          .missedLines(missedLines)
          .build().get();
    }
  }

  private static class SegmentState{
    final boolean isActive;
    final int hits;
    final ColumnRangeType type;

    SegmentState(boolean isActive, int hits, ColumnRangeType type) {
      this.isActive = isActive;
      this.hits = hits;
      this.type = type;
    }

    @Override
    public boolean equals(Object obj){
      if(this == obj)
        return true;
      if(obj instanceof SegmentState other){
        return this.isActive == other.isActive
            && this.hits == other.hits
            && this.type == other.type;
      }
      return false;
    }
  }

  private static class BranchData{
    Range loc;
    List<Range> locations = new ArrayList<>();
  }

  private static class ResourcePath implements Closeable{

    private final Path path;
    private final FileSystem fileSystem;
    private final String representation;

    private ResourcePath(Path path, FileSystem fileSystem, String representation){
      this.path = path;
      this.fileSystem = fileSystem;
      this.representation = representation;
    }

    public static ResourcePath of(URI uri) throws IOException{
      String scheme = uri.getScheme();
      if("file".equalsIgnoreCase(scheme)){
        Path path = Path.of(uri).normalize();
        return new ResourcePath(path, null, path.toString());
      }
      if("jar".equalsIgnoreCase(scheme)){
        try{
          Path path = Path.of(uri).normalize();
          return new ResourcePath(path, null, path.toString());
        }catch (FileSystemNotFoundException e){
          FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
          return new ResourcePath(Path.of(uri), fs, uri.toString());
        }
      }
      throw new IOException("Unsupported URI scheme: " + scheme);
    }

    public Path getPath(){
      return path;
    }

    public String getStringPath(){
      return representation;
    }

    @Override
    public void close() throws IOException {
      if (fileSystem != null && fileSystem.isOpen()) {
        fileSystem.close();
      }
    }
  }

}



