package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageSourceFile;

import org.apache.commons.text.StringEscapeUtils;

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class GetFileContent extends GetFileContentTOP {

  public GetFileContent(long file){
    super(file);
  }

  @Override
  public SimpleResult<String> doAction(){
    Optional<String> result = getFileContent(this.getFileGemId());
    if(result.isEmpty()){
      return new SimpleResult<>(
          getId(), LoadingState.FAILED_TO_LOAD, String.class.getName());
    }
    return new SimpleResult<String>(getId(), result.get(), String.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<String> getFileContent(long fileGemId){
    // Try to get file and read content
    Optional<CoverageSourceFile> sourceFileOpt = CoverageDashboardManager.coverageSourceFileOpt(fileGemId);
    if(sourceFileOpt.isEmpty())
      return Optional.empty();
    CoverageSourceFile sourceFile = sourceFileOpt.get();

    try {
      String value = Files.readString(Path.of(sourceFile.getPhysicalPath()));
      return Optional.of(StringEscapeUtils.escapeJava(value));
    }catch(IOException ex){
      Log.error("Error while reading content of source-file: " + ex.getMessage());
      return Optional.empty();
    }

  }
}
