package coverage;

import mc.fenix.editor.monacoeditortypes.InlineDecoration;
import mc.fenix.editor.monacoeditortypes.InlineDecorationBuilder;
import mc.fenix.editor.monacoeditortypes.RangeBuilder;

import umlp.jsweet.extension.annotation.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CoverageViewer extends CoverageViewerTOP {
  
  
  @Override
  public List<InlineDecoration> getHighlights() {

    InlineDecoration decorationGreen = new InlineDecorationBuilder()
        .range(new RangeBuilder()
            .startLin(1)
			.startCol(1)
			.endLin(2)
			.endCol(5).build().get()
        )
        .cssClass("monacoExampleGreen")
        .build().get();
	
	InlineDecoration decorationRed = new InlineDecorationBuilder()
        .range(new RangeBuilder()
            .startLin(3)
			.startCol(6)
			.endLin(5)
			.endCol(8).build().get()
        )
        .cssClass("monacoExampleRed")
        .build().get();

    return Arrays.asList(decorationGreen, decorationRed);
  }
}