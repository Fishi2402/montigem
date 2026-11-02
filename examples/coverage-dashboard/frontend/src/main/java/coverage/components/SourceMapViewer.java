package coverage.components;

import coveragedashboard.CoverageSourceFile;
import mc.fenix.editor.monacoeditortypes.LineClickEvent;
import org.apache.commons.io.FilenameUtils;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class SourceMapViewer extends SourceMapViewerTOP{

  @Override
  public String getSourceLanguage(CoverageSourceFile targetFile){
    return "java";
  }

  @Override
  public Consumer<Object> onTargetClick(){
    return new Consumer<Object>() {
      @Override
      public void accept(Object o) {
        if(o instanceof LineClickEvent){
          // How to scroll now other instance? -> Binding required
        }
      }
    };
  }

  @Override
  public String getTargetLanguage(CoverageSourceFile targetFile) {
    return "typescript";
    /*
    int dotIndex = targetFile.getPath().lastIndexOf('.');
    String extension;
    if (dotIndex > 0 && dotIndex < targetFile.getPath().length() - 1)
      extension = targetFile.getPath().substring(dotIndex + 1);
    else
      return "java";

    switch(extension){
      case ".js":
      case ".jsx":
        return "javascript";
      case "ts":
      case "tsx":
        return "typescript";
      case ".java":
      case ".jav":
        return "java";
      default:
        return "java";
    }
    */

  }
}
