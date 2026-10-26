package coveragedashboard.ingestion;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import coveragedashboard.ArtifactCategory;
import coveragedashboard.ColumnRangeType;
import coveragedashboard.CoverageColumnRangeBuilder;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.MeasuredLine;
import coveragedashboard.MeasuredLineBuilder;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageScope;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.CoverageSourceFileBuilder;
import coveragedashboard.PerspectiveType;
import coveragedashboard.ReportType;
import coveragedashboard.util.ResourcePathHelper;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSource;
import de.monticore.sourcemap.DecodedSourceMap;
import de.monticore.sourcemap.Decoding;
import de.se_rwth.commons.logging.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class C8JsonCoverageParser implements CoverageReportParser {

  private static final String BUNDLE_PATH = "/dist/client/browser/";
  private static final String BUNDLED_UMLP_2_JAVA_PATH = "/frontend/build/app/umlpJava2ts/";
  private static final String UMLP_2_JAVA_PATH = "/frontend/build/umlpJava2ts/";
  private static final String BUNDLED_GUI_2_JAVA_2_TS_PATH = "/frontend/build/app/gui2java2ts/";
  private static final String GUI_2_JAVA_2_TS_PATH = "/frontend/build/gui2java2ts/";
  private static final String COMPONENT_OBSERVER_MANAGER_PATH = "/frontend/build/umlp2jsweetObserver/";

  private static final Pattern COMPONENT_OBSERVER_MANAGER_PATTERN =
      Pattern.compile("^.*/frontend/build/app/gui2java2ts/[^/]+ComponentObserverManager\\.ts");

  private final JsonFactory jsonFactory;
  private final CoverageModuleAggregator moduleAggregator;

  C8JsonCoverageParser() {
    this(new JsonFactory(), new CoverageModuleAggregator());
  }

  C8JsonCoverageParser(JsonFactory jsonFactory, CoverageModuleAggregator moduleAggregator) {
    this.jsonFactory = Objects.requireNonNull(jsonFactory, "jsonFactory must not be null");
    this.moduleAggregator = Objects.requireNonNull(moduleAggregator, "moduleAggregator must not be null");
  }

  @Override
  public CoverageReport parse(File jsonFile, String reportName, Path basePath, Path rootPath) throws IOException {
    Objects.requireNonNull(jsonFile, "jsonFile must not be null");
    Objects.requireNonNull(reportName, "reportName must not be null");

    try (ParseSession session = new ParseSession();
         JsonParser parser = jsonFactory.createParser(jsonFile)) {

      if (parser.nextToken() != JsonToken.START_OBJECT) {
        throw new IOException("Expected JSON Object root");
      }

      List<CoverageSourceFile> parsedFiles = new ArrayList<>();

      while (parser.nextToken() != JsonToken.END_OBJECT) {
        String outerKeyFilePath = parser.currentName();
        parser.nextToken();

        String internalPath = null;
        Map<String, Range> statementMap = new HashMap<>();
        Map<String, BranchData> branchMap = new HashMap<>();
        Map<String, Integer> statementHits = new HashMap<>();
        Map<String, List<Integer>> branchHits = new HashMap<>();
        Map<String, FnData> fnMap = new HashMap<>();
        Map<String, Integer> fnHits = new HashMap<>();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
          String fieldName = parser.currentName();
          JsonToken token = parser.nextToken();

          if ("path".equals(fieldName) && token == JsonToken.VALUE_STRING) {
            internalPath = parser.getText();
          } else if ("statementMap".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseStatementMap(parser, statementMap);
          } else if ("branchMap".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseBranchMap(parser, branchMap);
          } else if ("s".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseStatementHits(parser, statementHits);
          } else if ("b".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseBranchHits(parser, branchHits);
          } else if ("fnMap".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseFnMap(parser, fnMap);
          } else if ("f".equals(fieldName) && token == JsonToken.START_OBJECT) {
            parseFnHits(parser, fnHits);
          } else {
            parser.skipChildren();
          }
        }

        String effectiveFilePath = (internalPath != null && !internalPath.isBlank()) ? internalPath : outerKeyFilePath;

        if (effectiveFilePath != null && isValidSourceFile(effectiveFilePath, rootPath)) {
          parsedFiles.add(createSourceFile(
              session, effectiveFilePath, basePath, rootPath,
              statementMap, branchMap, statementHits, branchHits, fnMap, fnHits
          ));
        }
      }

      CoverageModule rootModule = moduleAggregator.groupFilesInModules(parsedFiles);

      CoveragePerspective directPerspective = CoverageDashboardManager.coveragePerspectiveBuilder()
          .name("TS")
          .rootModule(rootModule)
          .type(PerspectiveType.MEASURED_ROOT)
          .build()
          .get();

      // Aggregate java files
      CoverageModule javaModule = moduleAggregator.groupDependentFilesByExtension("java", parsedFiles);
      CoveragePerspective javaPerspective = CoverageDashboardManager.coveragePerspectiveBuilder()
          .name("JAVA")
          .rootModule(javaModule)
          .type(PerspectiveType.AGGREGATED)
          .build()
          .get();

      // Aggregate FTL files
      CoverageModule ftlModule = moduleAggregator.groupDependentFilesByExtension("ftl", parsedFiles);
      CoveragePerspective ftlPerspective = CoverageDashboardManager.coveragePerspectiveBuilder()
          .name("FTL")
          .rootModule(ftlModule)
          .type(PerspectiveType.AGGREGATED)
          .build()
          .get();


      return CoverageDashboardManager.coverageReportBuilder()
          .name(reportName)
          .type(ReportType.GENERATED_CODE_TEST)
          .directPerspective(directPerspective)
          .aggregatedPerspectivesAdd(javaPerspective)
          .aggregatedPerspectivesAdd(ftlPerspective)
          .build()
          .get();
    }
  }

  private CoverageSourceFile createSourceFile(
      ParseSession session,
      String filePath,
      Path basePath,
      Path rootPath,
      Map<String, Range> statementMap,
      Map<String, BranchData> branchMap,
      Map<String, Integer> statementHits,
      Map<String, List<Integer>> branchHits,
      Map<String, FnData> fnMap,
      Map<String, Integer> fnHits) throws IOException {

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

    try {
      fileBuilder.addAllGeneratedBys(resolveGenerationSources(session, fileBuilder, rootPath.toAbsolutePath().normalize()));
    } catch (URISyntaxException e) {
      Log.error("Unable to resolve source-map for " + fileBuilder.getPath() + ": " + e.getMessage());
    }

    return fileBuilder.build().get();
  }

  private List<CoverageSourceFile> resolveGenerationSources(
      ParseSession session,
      CoverageSourceFileBuilder targetFileBuilder,
      Path absoluteRootPath) throws IOException, URISyntaxException {

    List<CoverageSourceFile> generatedSources = new ArrayList<>();

    Path sourceMapPath = resolveSourceMapPath(targetFileBuilder.getPhysicalPath());
    if (sourceMapPath == null || !Files.exists(sourceMapPath)) {
      return generatedSources;
    }

    DecodedSourceMap sourceMap;
    try {
      String sourceMapContent = Files.readString(sourceMapPath);
      sourceMap = Decoding.parseSourceMapJson(
          sourceMapContent, sourceMapPath.getParent().toUri().toURL());
    } catch (InvalidPathException e) {
      Log.error("Error while parsing source-map for " + targetFileBuilder.getPath() + ": " + e.getMessage());
      return generatedSources;
    }

    List<DecodedMapping> decodedMappings = sourceMap.getMappings();

    Map<Integer, MeasuredLine> targetLineMap = targetFileBuilder.getLinesList().stream()
        .filter(MeasuredLine.class::isInstance)
        .map(MeasuredLine.class::cast)
        .collect(Collectors.toMap(MeasuredLine::getLineNumber, l -> l, (existing, replacement) -> existing));

    for (DecodedSource decodedSource : sourceMap.sources) {
      if (decodedSource.ignored || decodedSource.url == null) {
        continue;
      }

      ResourcePath resourcePath = ResourcePath.of(URI.create(decodedSource.url.toString().replace('\\', '/')));
      session.registerResource(resourcePath);

      Path decodedSourcePath = resourcePath.getPath().normalize();

      CoverageSourceFileBuilder sourceCoverageFile = CoverageDashboardManager.coverageSourceFileBuilder()
          .path(extractRelativePath(decodedSourcePath, absoluteRootPath))
          .name(extractFileName(decodedSourcePath))
          .physicalPath(resourcePath.getStringPath())
          .category(ArtifactCategory.GENERATED);

      Map<Integer, MeasuredLineBuilder> lineBuilders = new HashMap<>();

      Map<Integer, Set<Integer>> origToGenLines = new HashMap<>();

      for (DecodedMapping mapping : decodedMappings){
        if (mapping.originalSource != null && mapping.originalSource.url.sameFile(decodedSource.url)) {
          origToGenLines
              .computeIfAbsent(mapping.originalLine, k -> new HashSet<>())
              .add(mapping.generatedLine); // Set eliminates token-level duplicates on the same generated line!
        }
      }

      for(Map.Entry<Integer, Set<Integer>> entry : origToGenLines.entrySet()){
        int origLineNumber = entry.getKey();
        Set<Integer> genLines = entry.getValue();
        MeasuredLineBuilder line = lineBuilders.computeIfAbsent(
            origLineNumber,
            lineNumber -> CoverageDashboardManager.measuredLineBuilder()
                .lineNumber(lineNumber)
                .coveredInstructions(0)
                .missedInstructions(0)
                .coveredBranches(0)
                .missedBranches(0)
        );
        for(int genLineNumber : genLines){
          MeasuredLine targetLine = targetLineMap.get(genLineNumber);
          if(targetLine != null){
            line.missedInstructions(line.getMissedInstructions() + targetLine.getMissedInstructions());
            line.coveredInstructions(line.getCoveredInstructions() + targetLine.getCoveredInstructions());
            line.missedBranches(line.getMissedBranches() + targetLine.getMissedBranches());
            line.coveredBranches(line.getCoveredBranches() + targetLine.getCoveredBranches());
          }
        }
      }

      int fileCoveredInstructions = 0;
      int fileMissedInstructions = 0;
      int fileCoveredBranches = 0;
      int fileMissedBranches = 0;
      int fileCoveredLines = 0;
      int filePartialLines = 0;
      int fileMissedLines = 0;
      int fileUnmappedLines = 0;

      for (MeasuredLineBuilder lineBuilder : lineBuilders.values()) {
        sourceCoverageFile.linesAdd(lineBuilder.build().get());
        fileCoveredBranches += lineBuilder.getCoveredBranches();
        fileMissedBranches += lineBuilder.getMissedBranches();
        fileCoveredInstructions += lineBuilder.getCoveredInstructions();
        fileMissedInstructions += lineBuilder.getMissedInstructions();

        int totalCovered = lineBuilder.getCoveredInstructions() + lineBuilder.getCoveredBranches();
        int totalMissed = lineBuilder.getMissedInstructions() + lineBuilder.getMissedBranches();


        if (totalCovered > 0 && totalMissed > 0) {
          filePartialLines++;
        } else if (totalCovered > 0) {
          fileCoveredLines++;
        } else {
          fileMissedLines++;
        }
      }

      Optional<List<String>> fileLines;
      try{
        fileLines = ResourcePathHelper.withPath(
          resourcePath.representation,
          path -> Files.readAllLines(path));
      }catch (IOException e){
        Log.error("Could not read file to count lines: " + resourcePath.representation);
        continue;
      }
      if(fileLines.isPresent()){
        List<String> lines = fileLines.get();
        for(int i = 0; i < lines.size(); i++) {
          int lineNumber = i + 1;
          if(lines.get(i).isBlank()){
            continue;
          }
          if (!lineBuilders.containsKey(lineNumber)) {
            fileUnmappedLines++;
            sourceCoverageFile.linesAdd(CoverageDashboardManager.unmappedLineBuilder()
                .lineNumber(lineNumber).build().get());
          }
        }
      }

      if (sourceCoverageFile.getLinesList().isEmpty()) {
        continue;
      }

      CoverageMetrics fileMetrics = CoverageDashboardManager.coverageMetricsBuilder()
          .coveredInstructions(fileCoveredInstructions)
          .missedInstructions(fileMissedInstructions)
          .coveredBranches(fileCoveredBranches)
          .missedBranches(fileMissedBranches)
          .coveredLines(fileCoveredLines)
          .partialLines(filePartialLines)
          .missedLines(fileMissedLines)
          .unmappedLines(fileUnmappedLines)
          .build()
          .get();

      sourceCoverageFile.metrics(fileMetrics);

      try {
        sourceCoverageFile.addAllGeneratedBys(this.resolveGenerationSources(session, sourceCoverageFile, absoluteRootPath));
      } catch (URISyntaxException e) {
        Log.error("Unable to resolve nested source-map for " + sourceCoverageFile.getPath() + ": " + e.getMessage());
      }

      generatedSources.add(sourceCoverageFile.build().get());
    }

    return generatedSources;
  }

  private Path resolveSourceMapPath(String physicalPath) {
    if (physicalPath == null || physicalPath.isBlank()) {
      return null;
    }
    try {
      Path standardMap = Path.of(physicalPath + ".map");
      if (Files.exists(standardMap)) {
        return standardMap;
      }
      if (physicalPath.contains("/gui2java2ts/") || physicalPath.contains("\\gui2java2ts\\")) {
        String gui2javaPath = physicalPath
            .replace("/gui2java2ts/", "/gui2java/")
            .replace("\\gui2java2ts\\", "\\gui2java\\");
        Path splitMap = Path.of(gui2javaPath + ".map");
        if (Files.exists(splitMap)) {
          return splitMap;
        }
      }
    } catch (InvalidPathException ignored) {
      return null;
    }
    return null;
  }

  private String extractFileName(String fullPath) {
    if (fullPath == null || fullPath.isBlank()) {
      return "unknown";
    }
    return extractFileName(Path.of(fullPath));
  }

  private String extractFileName(Path fullPath) {
    Path fileName = fullPath.normalize().getFileName();
    return fileName != null ? fileName.toString() : "unknown";
  }

  private String extractRelativePath(String filePath, Path basePath) {
    if (filePath == null || filePath.isBlank()) {
      return "unknown";
    }
    return extractRelativePath(Path.of(filePath), basePath);
  }

  private String extractRelativePath(Path filePath, Path basePath) {
    if (basePath == null || !filePath.startsWith(basePath)) {
      return filePath.toString();
    }
    return basePath.relativize(filePath).toString();
  }

  private String extractPhysicalPath(String filePath, Path rootPath) {
    String normalizedFullPath = normalizePhysicalPath(filePath, rootPath);
    String physicalPath = normalizedFullPath.replace(BUNDLE_PATH, "/");

    if (COMPONENT_OBSERVER_MANAGER_PATTERN.matcher(physicalPath).matches()) {
      return physicalPath.replace(BUNDLED_GUI_2_JAVA_2_TS_PATH, COMPONENT_OBSERVER_MANAGER_PATH);
    }

    return physicalPath
        .replace(BUNDLED_GUI_2_JAVA_2_TS_PATH, GUI_2_JAVA_2_TS_PATH)
        .replace(BUNDLED_UMLP_2_JAVA_PATH, UMLP_2_JAVA_PATH);
  }

  private boolean isValidSourceFile(String filePath, Path rootPath){
    String path = normalizePhysicalPath(filePath, rootPath);
    // Check if this path is from either that gui2java2ts or umlpJava2ts and not some other component
    return path.contains(BUNDLED_GUI_2_JAVA_2_TS_PATH) || path.contains(BUNDLED_UMLP_2_JAVA_PATH);
  }

  private String normalizePhysicalPath(String path, Path rootPath){
    String normalizedFullPath = rootPath.resolve(Path.of(path))
        .toAbsolutePath()
        .normalize()
        .toString()
        .replace(File.separatorChar, '/');

    return normalizedFullPath.replace(BUNDLE_PATH, "/");
  }

  private void parseStatementMap(JsonParser parser, Map<String, Range> map) throws IOException {
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String statement = parser.currentName();
      parser.nextToken();
      Range range = parseRange(parser);
      if (range.isValid()) {
        map.put(statement, range);
      }
    }
  }

  private void parseBranchMap(JsonParser parser, Map<String, BranchData> map) throws IOException {
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String branch = parser.currentName();
      parser.nextToken();

      Range loc = null;
      List<Range> locations = new ArrayList<>();

      while (parser.nextToken() != JsonToken.END_OBJECT) {
        String name = parser.currentName();
        JsonToken token = parser.nextToken();
        if ("loc".equals(name)) {
          loc = parseRange(parser);
        } else if ("locations".equals(name) && token == JsonToken.START_ARRAY) {
          while (parser.nextToken() != JsonToken.END_ARRAY) {
            locations.add(parseRange(parser));
          }
        } else {
          parser.skipChildren();
        }
      }
      map.put(branch, new BranchData(loc, locations));
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
      parser.nextToken();

      List<Integer> hits = new ArrayList<>();
      while (parser.nextToken() != JsonToken.END_ARRAY) {
        hits.add(parser.getIntValue());
      }
      map.put(id, hits);
    }
  }

  private Range parseRange(JsonParser parser) throws IOException {
    int startLine = 0;
    int startColumn = 0;
    int endLine = 0;
    int endColumn = 0;

    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String name = parser.currentName();
      JsonToken token = parser.nextToken();

      if ("start".equals(name) && token == JsonToken.START_OBJECT) {
        while (parser.nextToken() != JsonToken.END_OBJECT) {
          String subName = parser.currentName();
          parser.nextToken();
          if ("line".equals(subName)) startLine = parser.getIntValue();
          else if ("column".equals(subName)) startColumn = parser.getIntValue();
          else parser.skipChildren();
        }
      } else if ("end".equals(name) && token == JsonToken.START_OBJECT) {
        while (parser.nextToken() != JsonToken.END_OBJECT) {
          String subName = parser.currentName();
          parser.nextToken();
          if ("line".equals(subName)) endLine = parser.getIntValue();
          else if ("column".equals(subName)) endColumn = parser.getIntValue();
          else parser.skipChildren();
        }
      } else {
        parser.skipChildren();
      }
    }
    return new Range(startLine, startColumn, endLine, endColumn);
  }

  private void parseFnMap(JsonParser parser, Map<String, FnData> map) throws IOException {
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String id = parser.currentName();
      parser.nextToken();

      String name = null;
      Range decl = null;
      Range loc = null;

      while (parser.nextToken() != JsonToken.END_OBJECT) {
        String fieldName = parser.currentName();
        JsonToken token = parser.nextToken();
        if ("name".equals(fieldName)) {
          name = parser.getText();
        } else if ("decl".equals(fieldName) && token == JsonToken.START_OBJECT) {
          decl = parseRange(parser);
        } else if ("loc".equals(fieldName) && token == JsonToken.START_OBJECT) {
          loc = parseRange(parser);
        } else {
          parser.skipChildren();
        }
      }
      map.put(id, new FnData(name, decl, loc));
    }
  }

  private void parseFnHits(JsonParser parser, Map<String, Integer> map) throws IOException {
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String id = parser.currentName();
      parser.nextToken();
      map.put(id, parser.getIntValue());
    }
  }

  private void deduplicateRanges(MeasuredLineBuilder lineBuilder, List<CoverageColumnRangeBuilder> rawRanges) {
    if (rawRanges.isEmpty()) {
      return;
    }

    List<CoverageColumnRangeBuilder> baseRanges = new ArrayList<>();
    List<CoverageColumnRangeBuilder> overlayRanges = new ArrayList<>();

    List<CoverageColumnRangeBuilder> missedBranches = rawRanges.stream()
        .filter(r -> r.getType() == ColumnRangeType.BRANCH && r.getHits() == 0)
        .collect(Collectors.toList());

    for (CoverageColumnRangeBuilder r : rawRanges) {
      if (r.getType() == ColumnRangeType.BRANCH) {
        overlayRanges.add(r);
      } else if (r.getType() == ColumnRangeType.STATEMENT) {
        if (r.getHits() > 0) {
          boolean overlapsMissedBranch = missedBranches.stream().anyMatch(mb ->
              mb.getFromColumn() < r.getToColumn() && mb.getToColumn() > r.getFromColumn());
          if (overlapsMissedBranch) {
            r.type(ColumnRangeType.PARTIAL);
          }
        }
        baseRanges.add(r);
      } else {
        baseRanges.add(r);
      }
    }

    flattenLayer(lineBuilder, baseRanges, true);
    flattenLayer(lineBuilder, overlayRanges, false);
  }

  private void flattenLayer(MeasuredLineBuilder lineBuilder, List<CoverageColumnRangeBuilder> ranges, boolean isBaseLayer) {
    if (ranges.isEmpty()) {
      return;
    }

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

      SegmentState state = isBaseLayer
          ? evaluateBaseSegment(startCol, endCol, ranges)
          : evaluateOverlaySegment(startCol, endCol, ranges);

      if (!state.isActive()) {
        continue;
      }

      if (currentSegment != null && state.equals(currentState)) {
        currentSegment.toColumn(endCol);
      } else {
        if (currentSegment != null) {
          lineBuilder.columnRangesAdd(currentSegment.build().get());
        }
        currentSegment = CoverageDashboardManager.coverageColumnRangeBuilder()
            .fromColumn(startCol)
            .toColumn(endCol)
            .hits(state.hits())
            .type(state.type());
        currentState = state;
      }
    }
    if (currentSegment != null) {
      lineBuilder.columnRangesAdd(currentSegment.build().get());
    }
  }

  private SegmentState evaluateBaseSegment(int startCol, int endCol, List<CoverageColumnRangeBuilder> ranges) {
    boolean missedStatement = false;
    boolean isPartialBlock = false;
    boolean isActive = false;

    for (CoverageColumnRangeBuilder r : ranges) {
      if (r.getFromColumn() <= startCol && r.getToColumn() >= endCol) {
        isActive = true;
        if (r.getType() == ColumnRangeType.PARTIAL) {
          isPartialBlock = true;
        } else if (r.getType() == ColumnRangeType.STATEMENT) {
          if (r.getHits() == 0) {
            missedStatement = true;
          }
        }
      }
    }

    if (!isActive) {
      return new SegmentState(false, 0, null);
    }
    if (isPartialBlock) {
      return new SegmentState(true, 1, ColumnRangeType.PARTIAL);
    }
    if (missedStatement) {
      return new SegmentState(true, 0, ColumnRangeType.STATEMENT);
    }
    return new SegmentState(true, 1, ColumnRangeType.STATEMENT);
  }

  private SegmentState evaluateOverlaySegment(int startCol, int endCol, List<CoverageColumnRangeBuilder> ranges) {
    boolean missedBranch = false;
    boolean isActive = false;

    for (CoverageColumnRangeBuilder r : ranges) {
      if (r.getFromColumn() <= startCol && r.getToColumn() >= endCol) {
        isActive = true;
        if (r.getHits() == 0) {
          missedBranch = true;
        }
      }
    }

    if (!isActive) {
      return new SegmentState(false, 0, null);
    }
    if (missedBranch) {
      return new SegmentState(true, 0, ColumnRangeType.BRANCH);
    }
    return new SegmentState(true, 1, ColumnRangeType.BRANCH);
  }

  private void distributeRange(Range range, int hits, ColumnRangeType type, FileCoverageContext context) {
    if (!range.isValid()) {
      return;
    }
    if (range.startLine() == range.endLine()) {
      applyToLine(range.startLine(), range.startColumn(), range.endColumn(), hits, type, true, context);
      return;
    }
    // Only count line metrics once, subsequent lines for visual representation
    applyToLine(range.startLine(), range.startColumn(), Integer.MAX_VALUE, hits, type, true, context);
    for (int line = range.startLine() + 1; line < range.endLine(); line++) {
      applyToLine(line, 0, Integer.MAX_VALUE, hits, type, false, context);
    }
    applyToLine(range.endLine(), 0, range.endColumn(), hits, type, false, context);
  }

  private void applyToLine(
      int lineNumber,
      int startCol,
      int endCol,
      int hits,
      ColumnRangeType type,
      boolean countLineMetrics,
      FileCoverageContext context) {

    MeasuredLineBuilder lineBuilder = context.getLineBuilder(lineNumber);
    context.addRawRange(lineNumber, startCol, endCol, hits, type);

    if(!countLineMetrics){
      // Only add ranges for visual representation, avoid double counting
      return;
    }

    if (type == ColumnRangeType.STATEMENT) {
      if (hits > 0) {
        lineBuilder.coveredInstructions(lineBuilder.getCoveredInstructions() + 1);
      } else {
        lineBuilder.missedInstructions(lineBuilder.getMissedInstructions() + 1);
      }
    } else if(type == ColumnRangeType.BRANCH){
      if (hits > 0) {
        lineBuilder.coveredBranches(lineBuilder.getCoveredBranches() + 1);
      } else {
        lineBuilder.missedBranches(lineBuilder.getMissedBranches() + 1);
      }
    }
  }

  private void finalizeLinesAndDeduplicate(FileCoverageContext context) {
    for (Map.Entry<Integer, MeasuredLineBuilder> entry : context.lines.entrySet()) {
      int lineNumber = entry.getKey();
      MeasuredLineBuilder lineBuilder = entry.getValue();
      List<CoverageColumnRangeBuilder> rawRanges = context.rawRangesPerLine.getOrDefault(lineNumber, List.of());
      deduplicateRanges(lineBuilder, rawRanges);
      context.metrics.addLine(lineBuilder);
    }
  }

  private void extractFunctionScopes(Map<String, FnData> fnMap, Map<String, Integer> fnHits, FileCoverageContext context) {
    for (Map.Entry<String, FnData> entry : fnMap.entrySet()) {
      FnData fnData = entry.getValue();
      Range effectiveScopeRange = fnData.getEffectiveRange();
      if (effectiveScopeRange == null || !effectiveScopeRange.isValid()) {
        continue;
      }

      int hits = fnHits.getOrDefault(entry.getKey(), 0);
      if (hits == 0) {
        context.uncoveredDeclarations.add(effectiveScopeRange);
      }

      CoverageMetrics scopeMetrics = CoverageDashboardManager.coverageMetricsBuilder()
          .coveredInstructions(0)
          .missedInstructions(0)
          .coveredBranches(0)
          .missedBranches(0)
          .coveredLines(0)
          .partialLines(0)
          .missedLines(0)
          .unmappedLines(0)
          .build()
          .get();

      context.scopes.add(CoverageDashboardManager.coverageScopeBuilder()
          .name(fnData.name() != null ? fnData.name() : "anonymous")
          .startLine(effectiveScopeRange.startLine())
          .startColumn(effectiveScopeRange.startColumn())
          .endLine(effectiveScopeRange.endLine())
          .endColumn(effectiveScopeRange.endColumn())
          .metrics(scopeMetrics)
          .build()
          .get());
    }
  }

  private void processStatements(Map<String, Range> statementMap, Map<String, Integer> statementHits, FileCoverageContext context) {
    for (Map.Entry<String, Range> entry : statementMap.entrySet()) {
      Range range = entry.getValue();
      int hits = statementHits.getOrDefault(entry.getKey(), 0);

      if (hits > 0 && isInsideUncoveredDeclaration(range, context.uncoveredDeclarations)) {
        hits = 0; // Dismiss hit if triggered only during class import/signature evaluation
      }

      context.metrics.addInstruction(hits);
      distributeRange(range, hits, ColumnRangeType.STATEMENT, context);
    }
  }

  private void processBranches(Map<String, BranchData> branchMap, Map<String, List<Integer>> branchHits, FileCoverageContext context) {
    for (Map.Entry<String, BranchData> entry : branchMap.entrySet()) {
      BranchData data = entry.getValue();
      List<Integer> hitsList = branchHits.getOrDefault(entry.getKey(), List.of(0));

      boolean hasHits = false;
      boolean hasMiss = false;

      for (int i = 0; i < data.locations().size(); i++) {
        Range range = data.locations().get(i);
        int hits = i < hitsList.size() ? hitsList.get(i) : 0;

        // Skip phantom/empty branch ranges produced by C8 for implicit-else or non-covered short-circuits
        if (range == null || !range.isValid()) {
          continue;
        }

        if (hits > 0) {
          hasHits = true;
        } else {
          hasMiss = true;
        }

        context.metrics.addBranch(hits);
        distributeRange(range, hits, ColumnRangeType.BRANCH, context);
      }

      if (hasHits && hasMiss && data.loc() != null && data.loc().isValid()) {
        distributeRange(data.loc(), 0, ColumnRangeType.PARTIAL, context);
      }
    }
  }

  private boolean isInsideUncoveredDeclaration(Range range, List<Range> uncoveredDeclarations) {
    return uncoveredDeclarations.stream().anyMatch(range::isEnclosedIn);
  }

  // --- Supporting Domain & State Classes ---

  private record Range(int startLine, int startColumn, int endLine, int endColumn) {
    public boolean isValid() {
      return startLine > 0 && endLine >= startLine;
    }

    public boolean isEnclosedIn(Range outer) {
      return this.startLine >= outer.startLine && this.endLine <= outer.endLine;
    }
  }

  private record FnData(String name, Range decl, Range loc) {
    /**
     * Resolves the primary range for this function scope.
     * Prefers declaration range, but falls back to execution body location (loc)
     * if the declaration is null or degenerate (common in arrow functions and expressions).
     */
    public Range getEffectiveRange() {
      if (decl != null && decl.isValid()) {
        return decl;
      }
      if (loc != null && loc.isValid()) {
        return loc;
      }
      return null;
    }
  }

  private record BranchData(Range loc, List<Range> locations) {}

  private record SegmentState(boolean isActive, int hits, ColumnRangeType type) {}

  private static class FileCoverageContext {
    final Map<Integer, MeasuredLineBuilder> lines = new HashMap<>();
    final Map<Integer, List<CoverageColumnRangeBuilder>> rawRangesPerLine = new HashMap<>();
    final List<Range> uncoveredDeclarations = new ArrayList<>();
    final Set<CoverageScope> scopes = new HashSet<>();
    final CoverageMetricsAccumulator metrics = new CoverageMetricsAccumulator();

    MeasuredLineBuilder getLineBuilder(int lineNumber) {
      return lines.computeIfAbsent(lineNumber, l -> CoverageDashboardManager.measuredLineBuilder()
          .lineNumber(l)
          .coveredInstructions(0)
          .missedInstructions(0)
          .coveredBranches(0)
          .missedBranches(0));
    }

    void addRawRange(int lineNumber, int startCol, int endCol, int hits, ColumnRangeType type) {
      rawRangesPerLine.computeIfAbsent(lineNumber, l -> new ArrayList<>())
          .add(CoverageDashboardManager.coverageColumnRangeBuilder()
              .fromColumn(startCol)
              .toColumn(endCol)
              .hits(hits)
              .type(type));
    }
  }

  private static class CoverageMetricsAccumulator {
    private int coveredInstructions;
    private int missedInstructions;
    private int coveredBranches;
    private int missedBranches;
    private int coveredLines;
    private int partialLines;
    private int missedLines;

    void addInstruction(int hits) {
      if (hits > 0) {
        coveredInstructions++;
      } else {
        missedInstructions++;
      }
    }

    void addBranch(int hits) {
      if (hits > 0) {
        coveredBranches++;
      } else {
        missedBranches++;
      }
    }

    void addLine(MeasuredLineBuilder lineBuilder) {
      int totalCovered = lineBuilder.getCoveredInstructions() + lineBuilder.getCoveredBranches();
      int totalMissed = lineBuilder.getMissedInstructions() + lineBuilder.getMissedBranches();

      if (totalCovered > 0 && totalMissed > 0) {
        partialLines++;
      }else if(totalCovered > 0) {
        coveredLines++;
      } else {
        missedLines++;
      }
    }

    CoverageMetrics build() {
      return CoverageDashboardManager.coverageMetricsBuilder()
          .coveredInstructions(coveredInstructions)
          .missedInstructions(missedInstructions)
          .coveredBranches(coveredBranches)
          .missedBranches(missedBranches)
          .coveredLines(coveredLines)
          .partialLines(partialLines)
          .missedLines(missedLines)
          .unmappedLines(0)
          .build()
          .get();
    }
  }

  private static final class ParseSession implements AutoCloseable {
    private final List<ResourcePath> openResources = new ArrayList<>();

    void registerResource(ResourcePath resource) {
      if (resource != null) {
        openResources.add(resource);
      }
    }

    @Override
    public void close() {
      for (ResourcePath resource : openResources) {
        try {
          resource.close();
        } catch (IOException e) {
          Log.warn("Failed to close resource: " + resource.getStringPath(), e);
        }
      }
      openResources.clear();
    }
  }

  private static final class ResourcePath implements Closeable {
    private final Path path;
    private final FileSystem fileSystem;
    private final String representation;

    private ResourcePath(Path path, FileSystem fileSystem, String representation) {
      this.path = path;
      this.fileSystem = fileSystem;
      this.representation = representation;
    }

    public static ResourcePath of(URI uri) throws IOException {
      String scheme = uri.getScheme();
      if ("file".equalsIgnoreCase(scheme)) {
        Path path = Path.of(uri).normalize();
        return new ResourcePath(path, null, path.toString());
      }
      if ("jar".equalsIgnoreCase(scheme)) {
        FileSystem fs = null;
        Path path;
        try {
          path = Path.of(uri).normalize();
        } catch (FileSystemNotFoundException e) {
          fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
          path = Path.of(uri).normalize();
        }
        String representation = uri.toString();
        int bangIndex = representation.indexOf('!');
        if(bangIndex == -1){
          throw new IOException("JAR scheme without bang:" + representation);
        }
        String jarUri = representation.substring(4, bangIndex);
        Path jar = Path.of(URI.create(jarUri));
        Path combined = jar.getFileName();
        for(Path element : path){
          combined = combined.resolve(element.toString());
        }

        return new ResourcePath(combined, fs, representation);
      }
      throw new IOException("Unsupported URI scheme: " + scheme);
    }

    public Path getPath() {
      return path;
    }

    public String getStringPath() {
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
