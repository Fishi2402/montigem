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
            // Monaco Editor columns are 1-based, whereas C8/istanbul columns are 0-based
            Range range = new Range(
                line.getLineNumber(),
                lineRange.getFromColumn() + 1,
                line.getLineNumber(),
                lineRange.getToColumn() + 1
            );
            decorations.add(new InlineDecoration(range, css));
          }
        } else {
          // coarse data, based on hits per line
          String css = determineCoarseCss(line);
          // Monaco line-covering range from column 1 to next line column 1
          Range range = new Range(line.getLineNumber(), 1, line.getLineNumber() + 1, 1);
          decorations.add(new InlineDecoration(range, css));
        }
      }
    }

    return decorations;
  }

  private static String determineCoarseCss(MeasuredLine line){
    boolean hasMiss = line.getMissedInstructions() > 0 || line.getMissedBranches() > 0;
    boolean hasCov = line.getCoveredInstructions() > 0 || line.getCoveredBranches() > 0;
    if (hasMiss && hasCov) {
      return "range-partial";
    }
    if (hasMiss) {
      return "range-missed";
    }
    return "range-covered";
  }
}
