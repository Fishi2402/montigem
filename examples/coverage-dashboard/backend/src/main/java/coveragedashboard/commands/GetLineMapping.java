package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageSourceFile;
import de.se_rwth.commons.logging.Log;
import umlp.backendrte.command.ListResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.io.IOException;
import java.net.URISyntaxException;
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
      java.nio.file.Path sourceMapPath = java.nio.file.Path.of(sourceFile.getPhysicalPath() + ".map");
      String sourceMapContent = java.nio.file.Files.readString(sourceMapPath);
      de.monticore.sourcemap.DecodedSourceMap sourceMap = de.monticore.sourcemap.Decoding.parseSourceMapJson(
          sourceMapContent, sourceMapPath.getParent().toUri().toURL());

      // Lookup map for path to file
      Map<String, CoverageSourceFile> pathToFileMap = sourceFile.getGeneratedByList().stream()
          .collect(Collectors.toMap(file -> file.getPhysicalPath(), file-> file));

      List<String> mappingList = new ArrayList<>();

      for(de.monticore.sourcemap.DecodedMapping mapping : sourceMap.getMappings()){
        if (mapping.originalSource == null || mapping.originalSource.url == null)
          continue;

        String originalPath = java.nio.file.Path.of(java.net.URI.create(mapping.originalSource.url.toString().replace('\\', '/'))).normalize().toString();

       CoverageSourceFile originalFile = pathToFileMap.get(originalPath);
       if(originalFile != null){
         String entry = mapping.generatedLine + "::" + originalFile.getGemId() + "::" + mapping.originalLine;
         mappingList.add(entry);
       }
      }
      return Optional.of(mappingList);
    }catch(IOException ex){
      Log.warn("Error while calculating line mapping: " + ex.getMessage());
      return Optional.empty();
    }
  }
}
