package coveragedashboard.commands;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageSourceFile;

import coveragedashboard.StringContent;
import de.se_rwth.commons.logging.Log;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class GetSourceFileContent extends GetSourceFileContentTOP{

  public GetSourceFileContent(Long file){
    super(file);
  }

  @Override
  public SimpleResult<StringContent> doAction(){
    Optional<StringContent> result = getSourceFileContent(this.getFileGemId());
    if(result.isEmpty()){
      return new SimpleResult<>(
          getId(), LoadingState.FAILED_TO_LOAD, StringContent.class.getName());
    }
    return new SimpleResult<>(getId(), result.get(), StringContent.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<StringContent> getSourceFileContent(long fileGemId){
    // Try to get file and read content
    Optional<CoverageSourceFile> sourceFileOpt = CoverageDashboardManager.coverageSourceFileOpt(fileGemId);
    if(sourceFileOpt.isEmpty())
      return Optional.empty();
    CoverageSourceFile sourceFile = sourceFileOpt.get();

    // Check if there is a source-map for this file
    Path sourceMap = Path.of(sourceFile.getPhysicalPath() + ".map");
    if(sourceMap.getFileName().endsWith("TOP")){
      StringContent value = CoverageDashboardManager.stringContentBuilder().content("Source-map for TOP not implemented yet.").build().get();
      return Optional.of(value);
    }

    if(Files.exists(sourceMap)){
      try {
        JsonFactory factory = new JsonFactory();

        String sourcePath = "";
        try(JsonParser parser = factory.createParser(new File(sourceMap.toUri()))){
          if(parser.nextToken() != JsonToken.START_OBJECT)
            throw new IOException("Expected JSON Object root");

          while(parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            JsonToken token = parser.nextToken();
            if ("sources".equals(field) && token == JsonToken.START_ARRAY) {
              if (parser.nextToken() == JsonToken.VALUE_STRING) {
                String relativeSource = parser.getText();
                relativeSource = "..\\" + relativeSource;
                sourcePath = sourceMap.getParent()
                    .resolve(relativeSource)
                    .normalize()
                    .toString();
                break;
              }
            } else {
              parser.skipChildren();
            }
          }
        }

        StringContent value = CoverageDashboardManager.stringContentBuilder().content(Files.readString(Path.of(sourcePath))).build().get();
        return Optional.of(value);
      }catch(IOException ex){
        Log.error("Error while reading content of source-file: " + ex.getMessage());
        return Optional.empty();
      }
    }else{
      StringContent value = CoverageDashboardManager.stringContentBuilder().content("No source-file available.").build().get();
      return Optional.of(value);
    }
  }
}
