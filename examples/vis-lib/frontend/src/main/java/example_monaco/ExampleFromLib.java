package example_monaco;

import mc.fenix.editor.monacoeditortypes.InlineDecoration;
import mc.fenix.editor.monacoeditortypes.InlineDecorationBuilder;
import umlp.jsweet.extension.annotation.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ExampleFromLib extends ExampleFromLibTOP {

  @Override
  public List<InlineDecoration> getHighlights() {

    InlineDecoration decoration = new InlineDecorationBuilder()
        .startLin(0)
        .startCol(20)
        .endLin(4)
        .endCol(10)
        .cssClass("monacoExample")
        .build().get();

    return Arrays.asList(decoration);
  }


}
