package example_monaco;

import de.se_rwth.commons.logging.Log;
import mc.fenix.editor.monacoeditortypes.HoverContent;
import mc.fenix.editor.monacoeditortypes.HoverContentBuilder;
import mc.fenix.editor.monacoeditortypes.InlineDecoration;
import mc.fenix.editor.monacoeditortypes.InlineDecorationBuilder;
import mc.fenix.editor.monacoeditortypes.RangeBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ExampleFromLib extends ExampleFromLibTOP {

  @Override
  public List<InlineDecoration> getHighlights() {

    InlineDecoration decoration = new InlineDecorationBuilder()
        .range(new RangeBuilder()
            .startLin(0)
                .startCol(20)
                .endLin(4)
                .endCol(10).build().get()
        )
        .cssClass("monacoExample")
        .build().get();

    return Arrays.asList(decoration);
  }

  public List<HoverContent> getHoverContents() {
    Log.warn("Setting Hover Contents.");
    HoverContent content = new HoverContentBuilder()
        .range(new RangeBuilder()
            .startLin(1)
            .startCol(1)
            .endLin(3)
            .endCol(53).build().get())
        .hoverContent(Arrays.asList("**Test**", "Does it work?")).build().get();

    return Arrays.asList(content);
  }


}
