package coverage.components;

import coveragedashboard.CoverageColumnRange;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageSourceFile;
import mc.fenix.editor.monacoeditortypes.InlineDecoration;
import mc.fenix.editor.monacoeditortypes.Range;
import mc.fenix.editor.monacoeditortypes.RangeBuilder;

import java.util.ArrayList;
import java.util.List;

public final class SourceViewerUtil {

  private  SourceViewerUtil(){
  }

  public static List<InlineDecoration> calculateDecoration(CoverageSourceFile file){
    // Collect raw segments with corresponding css-class
    List<DecorationSegment> segments = new ArrayList<>();
    for(CoverageLine line : file.getLinesList()){
      if(!line.getColumnRangesList().isEmpty()){
        // We have range data for the line
        for(CoverageColumnRange lineRange : line.getColumnRangesList()){
          String css = lineRange.getHits() > 0 ? "range-covered" : "range-missed";
          Range range = new Range(line.getLineNumber(), lineRange.getFromColumn(), line.getLineNumber(), lineRange.getToColumn());
          segments.add(new DecorationSegment(range, css));
        }
      }else{
        // coarse data, based on hits per line
        String css = determineCoarseCss(line);
        Range range = new Range(line.getLineNumber(), 0, line.getLineNumber() + 1, 0);
        segments.add(new DecorationSegment(range, css));
      }
    }
    // Merge adjacent segments for better UI-experience
    List<InlineDecoration> decorations = new ArrayList<>();
    RangeBuilder currentBuilder = null;
    String currentCss = null;

    for(DecorationSegment segment : segments){
      if(currentCss != null && currentCss.equals(segment.css)){
        currentBuilder.endLin(segment.range.getEndLin()).endCol(segment.range.getEndCol());
      }else{
        if (currentBuilder != null) {
          decorations.add(new InlineDecoration(currentBuilder.build().get(), currentCss));
        }
        currentCss = segment.css;
        currentBuilder = new RangeBuilder()
            .startLin(segment.range.getStartLin())
            .startCol(segment.range.getStartCol())
            .endLin(segment.range.getEndLin())
            .endCol(segment.range.getEndCol());
      }
    }

    if (currentBuilder != null) {
      decorations.add(new InlineDecoration(currentBuilder.build().get(), currentCss));
    }


    return decorations;
  }

  private static String determineCoarseCss(CoverageLine line){
    if (line.getMissedInstructions() > 0 || line.getMissedBranches() > 0) {
      return (line.getCoveredInstructions() > 0 || line.getMissedBranches() > 0)
          ? "range-partial"
          : "range-missed";
    }
    return "range-covered";
  }

  private static class DecorationSegment{
    final Range range;
    final String css;

    DecorationSegment(Range range, String css){
      this.range = range;
      this.css = css;
    }
  }
}
