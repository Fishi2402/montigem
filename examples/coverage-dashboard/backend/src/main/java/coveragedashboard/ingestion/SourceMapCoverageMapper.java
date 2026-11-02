package coveragedashboard.ingestion;

import coveragedashboard.ArtifactCategory;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.CoverageSourceFileBuilder;
import coveragedashboard.MeasuredLine;
import coveragedashboard.MeasuredLineBuilder;
import coveragedashboard.util.ResourcePathHelper;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSource;
import de.monticore.sourcemap.DecodedSourceMap;
import de.monticore.sourcemap.Decoding;
import de.se_rwth.commons.logging.Log;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SourceMapCoverageMapper implements AutoCloseable {

  private final ParseSession session;

  public SourceMapCoverageMapper() {
    this.session = new ParseSession();
  }

  public List<CoverageSourceFile> resolveGenerationSources(
      CoverageSourceFileBuilder targetFileBuilder,
      Path absoluteRootPath) throws IOException, URISyntaxException {
    return resolveGenerationSources(
        targetFileBuilder.getLinesList(),
        targetFileBuilder.getPhysicalPath(),
        targetFileBuilder.getName(),
        targetFileBuilder.getPath(),
        absoluteRootPath
    );
  }

  public List<CoverageSourceFile> resolveGenerationSources(
      List<CoverageLine> targetLines,
      String physicalPath,
      String targetFileName,
      String targetFilePath,
      Path absoluteRootPath) throws IOException {

    List<CoverageSourceFile> generatedSources = new ArrayList<>();

    Path sourceMapPath = resolveSourceMapPath(physicalPath);
    if (sourceMapPath == null || !Files.exists(sourceMapPath)) {
      return generatedSources;
    }

    DecodedSourceMap sourceMap;
    String sourceMapContent;
    try {
      sourceMapContent = Files.readString(sourceMapPath);
      sourceMap = Decoding.parseSourceMapJson(
          sourceMapContent, sourceMapPath.getParent().toUri().toURL());
    } catch (InvalidPathException e) {
      Log.error("Error while parsing source-map for " + targetFilePath + ": " + e.getMessage());
      return generatedSources;
    }

    List<DecodedMapping> decodedMappings = normalizeMappings(
        sourceMap, sourceMapContent, targetFileName);

    Map<Integer, MeasuredLine> targetLineMap = targetLines.stream()
        .filter(MeasuredLine.class::isInstance)
        .map(MeasuredLine.class::cast)
        .collect(Collectors.toMap(MeasuredLine::getLineNumber, l -> l, (existing, replacement) -> existing));

    // Pre-group all mappings by generated line sorted by column across all sources
    Map<Integer, List<DecodedMapping>> allMappingsByGenLine = decodedMappings.stream()
        .filter(m -> m.originalSource() != null)
        .sorted(java.util.Comparator.comparingInt(DecodedMapping::generatedColumn))
        .collect(Collectors.groupingBy(DecodedMapping::generatedLine));

    for (DecodedSource decodedSource : sourceMap.sources()) {
      if (decodedSource.ignored() || decodedSource.url() == null) {
        continue;
      }

      ResourcePath resourcePath = ResourcePath.of(URI.create(decodedSource.url().toString().replace('\\', '/')));
      session.registerResource(resourcePath);

      Path decodedSourcePath = resourcePath.getPath().normalize();

      CoverageSourceFileBuilder sourceCoverageFile = CoverageDashboardManager.coverageSourceFileBuilder()
          .path(extractRelativePath(decodedSourcePath, absoluteRootPath))
          .name(extractFileName(decodedSourcePath))
          .physicalPath(resourcePath.getStringPath())
          .category(ArtifactCategory.GENERATED);

      Map<Integer, MeasuredLineBuilder> lineBuilders = new HashMap<>();

      // Group mappings referencing this source by generated line
      Map<Integer, List<DecodedMapping>> sourceMappingsByGenLine = new HashMap<>();
      for (DecodedMapping mapping : decodedMappings) {
        if (mapping.originalSource() != null && mapping.originalSource().url().sameFile(decodedSource.url())) {
          sourceMappingsByGenLine.computeIfAbsent(mapping.generatedLine(), k -> new ArrayList<>()).add(mapping);
        }
      }

      // Track line-based coverage status per original line: [0] = hasCovered, [1] = hasMissed
      Map<Integer, boolean[]> origLineStatus = new HashMap<>();

      for (Map.Entry<Integer, List<DecodedMapping>> entry : sourceMappingsByGenLine.entrySet()) {
        int genLine = entry.getKey();
        List<DecodedMapping> sourceMappingsOnLine = entry.getValue();
        MeasuredLine targetLine = targetLineMap.get(genLine);
        if (targetLine == null) {
          continue;
        }

        List<DecodedMapping> allMappingsOnLine = allMappingsByGenLine.getOrDefault(genLine, List.of());
        List<coveragedashboard.CoverageColumnRange> columnRanges = targetLine.getColumnRangesList();

        if (columnRanges != null && !columnRanges.isEmpty() && allMappingsOnLine.size() > 1) {
          // Column range correlation for lines mapped from multiple sources/ranges
          for (DecodedMapping sm : sourceMappingsOnLine) {
            int origLine = sm.originalLine();
            boolean[] status = origLineStatus.computeIfAbsent(origLine, k -> new boolean[2]);

            int colStart = sm.generatedColumn();
            int colEnd = Integer.MAX_VALUE;
            int idx = allMappingsOnLine.indexOf(sm);
            if (idx >= 0 && idx < allMappingsOnLine.size() - 1) {
              colEnd = allMappingsOnLine.get(idx + 1).generatedColumn();
            }

            boolean segmentCovered = false;
            boolean segmentMissed = false;
            boolean hadOverlap = false;

            for (coveragedashboard.CoverageColumnRange cr : columnRanges) {
              if (Math.max(colStart, cr.getFromColumn()) < Math.min(colEnd, cr.getToColumn())) {
                hadOverlap = true;
                if (cr.getHits() > 0) {
                  segmentCovered = true;
                } else {
                  segmentMissed = true;
                }
              }
            }

            if (hadOverlap) {
              status[0] |= segmentCovered;
              status[1] |= segmentMissed;
            } else {
              boolean lineCov = targetLine.getCoveredInstructions() > 0 || targetLine.getCoveredBranches() > 0;
              boolean lineMiss = targetLine.getMissedInstructions() > 0 || targetLine.getMissedBranches() > 0;
              status[0] |= lineCov;
              status[1] |= lineMiss;
            }
          }
        } else {
          // Coarse line coverage: all mapped lines receive credit from the generated line
          boolean lineCov = targetLine.getCoveredInstructions() > 0 || targetLine.getCoveredBranches() > 0;
          boolean lineMiss = targetLine.getMissedInstructions() > 0 || targetLine.getMissedBranches() > 0;
          for (DecodedMapping sm : sourceMappingsOnLine) {
            int origLine = sm.originalLine();
            boolean[] status = origLineStatus.computeIfAbsent(origLine, k -> new boolean[2]);
            status[0] |= lineCov;
            status[1] |= lineMiss;
          }
        }
      }

      for (Map.Entry<Integer, boolean[]> entry : origLineStatus.entrySet()) {
        int origLine = entry.getKey();
        boolean hasCov = entry.getValue()[0];
        boolean hasMiss = entry.getValue()[1];

        if (!hasCov && !hasMiss) {
          continue;
        }

        MeasuredLineBuilder line = lineBuilders.computeIfAbsent(
            origLine,
            lineNumber -> CoverageDashboardManager.measuredLineBuilder()
                .lineNumber(lineNumber)
                .coveredInstructions(0)
                .missedInstructions(0)
                .coveredBranches(0)
                .missedBranches(0)
        );

        if (hasCov && hasMiss) {
          line.coveredInstructions(1);
          line.missedInstructions(1);
        } else if (hasCov) {
          line.coveredInstructions(1);
          line.missedInstructions(0);
        } else {
          line.coveredInstructions(0);
          line.missedInstructions(1);
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

      Optional<List<String>> fileLines = Optional.empty();
      try {
        fileLines = ResourcePathHelper.withPath(
            resourcePath.representation,
            path -> Files.readAllLines(path));
      } catch (IOException e) {
        if (decodedSource.content() != null) {
          fileLines = Optional.of(decodedSource.content().lines().collect(Collectors.toList()));
        } else {
          Log.error("Could not read file to count lines: " + resourcePath.representation);
          continue;
        }
      }
      if (fileLines.isEmpty() && decodedSource.content() != null) {
        fileLines = Optional.of(decodedSource.content().lines().collect(Collectors.toList()));
      }
      if (fileLines.isPresent()) {
        List<String> lines = fileLines.get();
        for (int i = 0; i < lines.size(); i++) {
          int lineNumber = i + 1;
          if (lines.get(i).isBlank()) {
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
          .coveredInstructions(fileCoveredLines)
          .missedInstructions(fileMissedLines)
          .coveredBranches(0)
          .missedBranches(0)
          .coveredLines(fileCoveredLines)
          .partialLines(filePartialLines)
          .missedLines(fileMissedLines)
          .unmappedLines(fileUnmappedLines)
          .build()
          .get();

      sourceCoverageFile.metrics(fileMetrics);

      try {
        sourceCoverageFile.addAllGeneratedBys(this.resolveGenerationSources(sourceCoverageFile, absoluteRootPath));
      } catch (URISyntaxException e) {
        Log.error("Unable to resolve nested source-map for " + sourceCoverageFile.getPath() + ": " + e.getMessage());
      }

      generatedSources.add(sourceCoverageFile.build().get());
    }

    return generatedSources;
  }

  public static Path resolveSourceMapPath(String physicalPath) {
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

  public static List<DecodedMapping> normalizeMappings(
      DecodedSourceMap sourceMap,
      String sourceMapContent,
      String targetFileName) {
    boolean isTargetTs = (targetFileName != null && targetFileName.endsWith(".ts"))
        || (sourceMap.file() != null && sourceMap.file().endsWith(".ts"));
    boolean startsWithA = Pattern.compile("\"mappings\"\\s*:\\s*\"A;").matcher(sourceMapContent).find();

    if (isTargetTs && startsWithA) {
      return sourceMap.mappings();
    }

    List<DecodedMapping> normalized = new ArrayList<>(sourceMap.mappings().size());
    for (DecodedMapping m : sourceMap.mappings()) {
      normalized.add(new DecodedMapping(
          m.generatedLine() + 1,
          m.generatedColumn(),
          m.originalSource(),
          m.originalLine() + 1,
          m.originalColumn(),
          m.name()
      ));
    }
    return normalized;
  }

  private static String extractFileName(Path fullPath) {
    Path fileName = fullPath.normalize().getFileName();
    return fileName != null ? fileName.toString() : "unknown";
  }

  private static String extractRelativePath(Path filePath, Path basePath) {
    if (basePath == null || !filePath.startsWith(basePath)) {
      return filePath.toString();
    }
    return basePath.relativize(filePath).toString();
  }

  @Override
  public void close() {
    session.close();
  }

  static final class ParseSession implements AutoCloseable {
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

  static final class ResourcePath implements Closeable {
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
          try {
            fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
            path = Path.of(uri).normalize();
          } catch (IOException ioException) {
            // Fallback to class loader
            int bangIndex = uri.toString().indexOf('!');
            if (bangIndex != -1) {
              String innerPath = uri.toString().substring(bangIndex + 2);
              URL url = ResourcePathHelper.class.getClassLoader().getResource(innerPath);
              if (url != null) {
                URI classpathURI = URI.create(url.toString().replace(" ", "%20"));
                try {
                  path = Path.of(classpathURI).normalize();
                  uri = classpathURI;
                } catch (Exception ex) {
                  // first time, open fs
                  fs = FileSystems.newFileSystem(classpathURI, Collections.emptyMap());
                  path = Path.of(classpathURI).normalize();
                  uri = classpathURI;
                }
              } else {
                throw ioException;
              }
            } else {
              throw ioException;
            }
          }
        }
        String representation = uri.toString();
        int bangIndex = representation.indexOf('!');
        if (bangIndex == -1) {
          throw new IOException("JAR scheme without bang:" + representation);
        }
        String jarUri = representation.substring(4, bangIndex);
        Path jar = Path.of(URI.create(jarUri));
        Path combined = jar.getFileName();
        for (Path element : path) {
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
