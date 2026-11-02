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
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
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

    URI uri;
    try {
      String path = sourceFile.getPhysicalPath().replace('\\', '/');
      if(path.matches("^[a-zA-Z]:/.*")){
        uri = Path.of(sourceFile.getPhysicalPath()).toUri();
      }else {
        uri = URI.create(sourceFile.getPhysicalPath());
      }
    } catch (IllegalArgumentException e) {
      uri = null;
    }

    String scheme = (uri != null) ? uri.getScheme() : null;
    Path path = null;
    FileSystem fs = null;

    try{
      if(scheme == null){
        path = Path.of(sourceFile.getPhysicalPath()).normalize();
      }else if ("file".equalsIgnoreCase(scheme)){
        path = Path.of(uri).normalize();
      }else if("jar".equalsIgnoreCase(scheme)){
        try{
          path = Path.of(uri).normalize();
        }catch (FileSystemNotFoundException e){
          try {
            fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
            path = Path.of(uri);
          } catch (IOException ex) {
            return Optional.empty();
          }
        }
      }
      if (path == null) {
        return Optional.empty();
      }

      String value = Files.readString(path);
      return Optional.of(StringEscapeUtils.escapeJava(value));

    }catch (IOException ex){
      Log.error("Error while reading content of source-file: " + ex.getMessage());
      return Optional.empty();
    }finally {
      if (fs != null && fs.isOpen()) {
        try {
          fs.close();
        } catch (IOException ignored) {
        }
      }
    }
  }
}
