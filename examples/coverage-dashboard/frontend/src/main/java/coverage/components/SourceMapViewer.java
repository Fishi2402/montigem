package coverage.components;

import coveragedashboard.App;
import coveragedashboard.CoverageColumnRange;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.StringContent;
import coveragedashboard.commands.GetFileContent;
import coveragedashboard.commands.GetSourceFileContent;
import mc.fenix.editor.monacoeditortypes.InlineDecoration;
import mc.fenix.editor.monacoeditortypes.LineClickEvent;
import mc.fenix.editor.monacoeditortypes.Range;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.SimpleResult;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


@Component
public class SourceMapViewer extends SourceMapViewerTOP{

  public SourceMapViewer(){
    super();
    this.updateEditors();
    this.setJumpSourceFileLine(0);
  }

  @Override
  public void setTargetFile(Optional<CoverageSourceFile> file){
    super.setTargetFile(file);

    this.updateEditors();
  }

  @Override
  public String getSourceLanguage(Optional<CoverageSourceFile> targetFile){
    return "java";
  }


  @Override
  public Consumer<Object> onTargetClick(){
    return (Object o)-> {
      if(o instanceof LineClickEvent) {
        // cast via instance of can not be transpiled via JSweet...
        LineClickEvent lineClickEvent = (LineClickEvent) o;

        this.setJumpSourceFileLine(lineClickEvent.getLineNumber());

      }
    };
  }

  @Override
  public String getTargetLanguage(Optional<CoverageSourceFile> targetFile) {
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

  @Override
  public List<InlineDecoration> getTargetHighlighting (Optional<CoverageSourceFile> fileOpt) {
    if(fileOpt.isEmpty())
      return null;

    CoverageSourceFile file = fileOpt.get();

    List<InlineDecoration> decorations = new ArrayList<>();
    for(CoverageLine line : file.getLinesList()){
      if(line.getColumnRangesList().size() > 0){
        // We have range data for the line
        for(CoverageColumnRange lineRange : line.getColumnRangesList()){
          Range range = new Range(line.getLineNumber(), lineRange.getFromColumn(), line.getLineNumber(), lineRange.getToColumn());
          InlineDecoration decoration = new InlineDecoration(range, "range-missed");
          if(lineRange.getHits() > 0){
            decoration.setCssClass("range-covered");
          }
          decorations.add(decoration);
        }
      }else{
        // coarse data, based on hits per line
        Range range = new Range(line.getLineNumber(), 0, line.getLineNumber() + 1, 0);
        InlineDecoration decoration = new InlineDecoration(range, "range-missed");
        if(line.getMissedInstructions() > 0 || line.getMissedBranches() > 0){
          if(line.getCoveredInstructions() > 0 || line.getMissedBranches() > 0){
            decoration.setCssClass("range-partial");
          }
        }else{
          decoration.setCssClass("range-covered");
        }
      }
    }
    return decorations;
  }


  // New target-file
  private void updateEditors(){
    if(this.getTargetFile().isEmpty()){
      this.setSourceContent("");
      this.setTargetContent("");
      // TODO: More cleanup like language, highlighting etc...
      return;
    }
    CoverageSourceFile file = this.getTargetFile().get();

    CommandResult<StringContent> result = CommandManager.executeCommand(new GetFileContent(file.getGemId()));
    SimpleResult<StringContent> simpleResult = result.asSimpleResult();
    String value = simpleResult.get().getContent();
    this.setTargetContent(value);
    result = CommandManager.executeCommand(new GetSourceFileContent(file.getGemId()));
    simpleResult = result.asSimpleResult();
    value = simpleResult.get().getContent();

    this.setSourceContent(value);
  }
}
