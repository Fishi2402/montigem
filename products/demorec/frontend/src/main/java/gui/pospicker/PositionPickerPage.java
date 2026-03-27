package gui.pospicker;

import java.util.function.Consumer;

public class PositionPickerPage extends PositionPickerPageTOP {

  public Consumer<String> changeCurrentPos(){
    return (String pixel) -> {
      this.setCurrentPos(pixel);
    };
  }
}
