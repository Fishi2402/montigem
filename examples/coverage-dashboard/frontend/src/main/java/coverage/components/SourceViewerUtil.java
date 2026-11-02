package coverage.components;

import coveragedashboard.ColumnRangeType;
import coveragedashboard.CoverageColumnRange;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.MeasuredLine;
import mc.fenix.editor.monacoeditortypes.InlineDecoration;
import mc.fenix.editor.monacoeditortypes.Range;

import java.util.ArrayList;
import java.util.List;

public final class SourceViewerUtil {

  private  SourceViewerUtil(){
  }

  public static List<InlineDecoration> calculateDecoration(CoverageSourceFile file){
    List<InlineDecoration> decorations = new ArrayList<>();

    // ----------------- Work around for batch-loading -----------------
    // Batch-load all lines
    CoverageDashboardManager.loadAllCoverageLineWithIDs(file._internal_getLinesGemList().getIDs());

    // Collect all ids of the ranges to batch-load them
    List<Long> coverageColumnIds = new ArrayList<>();
    for(CoverageLine rawLine : file.getLinesList()){
      // If nothing is collected for the line, we do not show anything
      if(rawLine instanceof MeasuredLine) {
        MeasuredLine line = (MeasuredLine) rawLine;
        coverageColumnIds.addAll(line._internal_getColumnRangesGemList().getIDs());
      }
    }
    // Batch-load all lines
    CoverageDashboardManager.loadAllCoverageColumnRangeWithIDs(coverageColumnIds);
    // ----------------- End Work around for batch-loading -----------------

    for(CoverageLine rawLine : file.getLinesList()){
      // If nothing is collected for the line, we do not show anything
      if(rawLine instanceof MeasuredLine) {
        MeasuredLine line = (MeasuredLine) rawLine;
        if (!line.getColumnRangesList().isEmpty()) {
          // We have range data for the line
          for (CoverageColumnRange lineRange : line.getColumnRangesList()) {
            String css;
            if (lineRange.getType() == ColumnRangeType.BRANCH) {
              css = lineRange.getHits() > 0 ? "range-covered-light" : "range-missed-light";
            } else if (lineRange.getType() == ColumnRangeType.PARTIAL) {
              css = "range-partial";
            } else {
              css = lineRange.getHits() > 0 ? "range-covered" : "range-missed";
            }
            Range range = new Range(line.getLineNumber(), lineRange.getFromColumn(), line.getLineNumber(), lineRange.getToColumn());
            decorations.add(new InlineDecoration(range, css));
          }
        } else {
          // coarse data, based on hits per line
          String css = determineCoarseCss(line);
          Range range = new Range(line.getLineNumber(), 0, line.getLineNumber() + 1, 0);
          decorations.add(new InlineDecoration(range, css));
        }
      }
    }

    return decorations;
  }

  private static String determineCoarseCss(MeasuredLine line){
    if (line.getMissedInstructions() > 0 || line.getMissedBranches() > 0) {
      return (line.getCoveredInstructions() > 0 || line.getCoveredBranches() > 0)
          ? "range-partial"
          : "range-missed";
    }
    return "range-covered";
  }
}
