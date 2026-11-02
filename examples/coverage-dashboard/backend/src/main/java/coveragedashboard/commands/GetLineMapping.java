package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageSourceFile;
import de.se_rwth.commons.logging.Log;
import jsweet.lang.Erased;
import umlp.backendrte.command.ListResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GetLineMapping extends  GetLineMappingTOP{

  public GetLineMapping(long file){
    super(file);
  }

  @Override
  public ListResult<String, List<String>> doAction(){
    Optional<List<String>> result = getLineMapping(this.getFileGemId());
    if(result.isEmpty())
      return ListResult.error(
          getId(), String.class.getName());

    return ListResult.of(getId(), result.get(), String.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<List<String>> getLineMapping(long fileGemId){
    Optional<CoverageSourceFile> sourceFileOpt =  CoverageDashboardManager.coverageSourceFileOpt(fileGemId);
    if(sourceFileOpt.isEmpty())
      return Optional.empty();
    CoverageSourceFile sourceFile = sourceFileOpt.get();
    try {
      java.nio.file.Path sourceMapPath = resolveSourceMapPath(sourceFile.getPhysicalPath());
      if(sourceMapPath == null || !java.nio.file.Files.exists(sourceMapPath)){
        // There is no source-map for this
        Log.warn("No source-map available for " + sourceFile.getPhysicalPath());
        return Optional.empty();
      }
      String sourceMapContent = java.nio.file.Files.readString(sourceMapPath);
      de.monticore.sourcemap.DecodedSourceMap sourceMap = de.monticore.sourcemap.Decoding.parseSourceMapJson(
          sourceMapContent, sourceMapPath.getParent().toUri().toURL());

      List<de.monticore.sourcemap.DecodedMapping> mappings = normalizeMappings(
          sourceMap, sourceMapContent, sourceFile.getName());

      // Lookup map for path to file
      Map<String, CoverageSourceFile> pathToFileMap = sourceFile.getGeneratedByList().stream()
          .collect(Collectors.toMap(file -> file.getPhysicalPath(), file-> file));

      List<String> mappingList = new ArrayList<>();

      for(de.monticore.sourcemap.DecodedMapping mapping : mappings){
        if (mapping.originalSource() == null || mapping.originalSource().url() == null)
          continue;

        String originalPath;
        if(mapping.originalSource().url().toString().startsWith("jar:"))
          originalPath = mapping.originalSource().url().toString().replace('\\', '/');
        else
          originalPath = java.nio.file.Path.of(java.net.URI.create(mapping.originalSource().url().toString().replace('\\', '/'))).normalize().toString();

       CoverageSourceFile originalFile = pathToFileMap.get(originalPath);
       if(originalFile != null){
         String entry = mapping.generatedLine() + "::" + originalFile.getGemId() + "::" + mapping.originalLine();
         mappingList.add(entry);
       }
      }
      return Optional.of(mappingList);
    }catch(IOException ex){
      Log.warn("Error while calculating line mapping: " + ex.getMessage());
      return Optional.empty();
    }
  }

  @Erased
  private java.nio.file.Path resolveSourceMapPath(String physicalPath) {
    if (physicalPath == null || physicalPath.isBlank()) {
      return null;
    }
    try {
      java.nio.file.Path standardMap = java.nio.file.Path.of(physicalPath + ".map");
      if (java.nio.file.Files.exists(standardMap)) {
        return standardMap;
      }
      if (physicalPath.contains("/gui2java2ts/") || physicalPath.contains("\\gui2java2ts\\")) {
        String gui2javaPath = physicalPath
            .replace("/gui2java2ts/", "/gui2java/")
            .replace("\\gui2java2ts\\", "\\gui2java\\");
        java.nio.file.Path splitMap = java.nio.file.Path.of(gui2javaPath + ".map");
        if (java.nio.file.Files.exists(splitMap)) {
          return splitMap;
        }
      }
    } catch (java.nio.file.InvalidPathException ignored) {
      return null;
    }
    return null;
  }

  /**
   * Normalizes source map mappings so that generatedLine and originalLine are always 1-based.
   * This is necessary because JSweet produces 1-based source maps (prepended with a dummy 'A'
   * mapping at line 0), so they do not need an adjustment. Standard MontiCore source maps
   * (e.g. Java -> FTL) follow the Source Map V3 specification and are 0-based, requiring
   * a +1 adjustment to match our 1-based line numbering domain model.
   */
  @Erased
  private static List<de.monticore.sourcemap.DecodedMapping> normalizeMappings(
      de.monticore.sourcemap.DecodedSourceMap sourceMap,
      String sourceMapContent,
      String targetFileName) {
    boolean isTargetTs = (targetFileName != null && targetFileName.endsWith(".ts"))
        || (sourceMap.file() != null && sourceMap.file().endsWith(".ts"));
    boolean startsWithA = java.util.regex.Pattern.compile("\"mappings\"\\s*:\\s*\"A;").matcher(sourceMapContent).find();

    if (isTargetTs && startsWithA) {
      return sourceMap.mappings();
    }

    List<de.monticore.sourcemap.DecodedMapping> normalized = new ArrayList<>(sourceMap.mappings().size());
    for (de.monticore.sourcemap.DecodedMapping m : sourceMap.mappings()) {
      normalized.add(new de.monticore.sourcemap.DecodedMapping(
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
}
