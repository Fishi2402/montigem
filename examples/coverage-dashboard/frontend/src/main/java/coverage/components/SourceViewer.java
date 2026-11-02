package coverage.components;

import coveragedashboard.CoverageColumnRange;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.commands.GetFileContent;
import mc.fenix.editor.monacoeditortypes.InlineDecoration;
import mc.fenix.editor.monacoeditortypes.Range;
import umlp.backendrte.command.CommandManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SourceViewer extends SourceViewerTOP{

  @Override
  public void setFile(Optional<CoverageSourceFile> file){
    super.setFile(file);

    this.updateEditor();
  }

  private void updateEditor(){
    if(this.getFile().isEmpty()) {
      this.setContent("");
      this.setDecoration(new ArrayList<>());
      return;
    }
    CoverageSourceFile file = this.getFile().get();
    this.setContent(CommandManager.executeCommand(new GetFileContent(file.getGemId())).asSimpleResult().get());

    this.setDecoration(calculateDecoration(file));
    this.setLanguage("Java");
  }

  private List<InlineDecoration> calculateDecoration(CoverageSourceFile file){
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
        decorations.add(decoration);
      }
    }
    return decorations;
  }
}
