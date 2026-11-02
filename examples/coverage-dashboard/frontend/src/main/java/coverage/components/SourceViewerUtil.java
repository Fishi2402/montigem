package coverage.components;

import coveragedashboard.ColumnRangeType;
import coveragedashboard.CoverageColumnRange;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageSourceFile;
import mc.fenix.editor.monacoeditortypes.InlineDecoration;
import mc.fenix.editor.monacoeditortypes.Range;

import java.util.ArrayList;
import java.util.List;

public final class SourceViewerUtil {

  private  SourceViewerUtil(){
  }

  public static List<InlineDecoration> calculateDecoration(CoverageSourceFile file){
    List<InlineDecoration> decorations = new ArrayList<>();
    for(CoverageLine line : file.getLinesList()){
      if(!line.getColumnRangesList().isEmpty()){
        // We have range data for the line
        for(CoverageColumnRange lineRange : line.getColumnRangesList()){
          String css;
          if(lineRange.getType() == ColumnRangeType.BRANCH){
            css = lineRange.getHits() > 0 ? "range-covered-light" : "range-missed-light";
          } else if (lineRange.getType() == ColumnRangeType.PARTIAL) {
            css = "range-partial";
          } else {
            css = lineRange.getHits() > 0 ? "range-covered" : "range-missed";
          }
          Range range = new Range(line.getLineNumber(), lineRange.getFromColumn(), line.getLineNumber(), lineRange.getToColumn());
          decorations.add(new InlineDecoration(range, css));
        }
      }else{
        // coarse data, based on hits per line
        String css = determineCoarseCss(line);
        Range range = new Range(line.getLineNumber(), 0, line.getLineNumber() + 1, 0);
        decorations.add(new InlineDecoration(range, css));
      }
    }
    return decorations;
  }

  private static String determineCoarseCss(CoverageLine line){
    if (line.getMissedInstructions() > 0 || line.getMissedBranches() > 0) {
      return (line.getCoveredInstructions() > 0 || line.getCoveredBranches() > 0)
          ? "range-partial"
          : "range-missed";
    }
    return "range-covered";
  }
}
