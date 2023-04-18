package stanley.tmp.io.input;

import umlp.jsweet.extension.annotation.Input;
import umlp.jsweet.extension.annotation.Output;

import java.util.function.Consumer;

public class GemButton {
  @Input
  protected String text = "";
  @Output
  protected Consumer leftClick;
}
