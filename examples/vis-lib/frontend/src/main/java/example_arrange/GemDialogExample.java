/* (c) https://github.com/MontiCore/monticore */
package example_arrange;

import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class GemDialogExample extends GemDialogExampleTOP {
  @Override
  public Consumer<Void> openDialog() {
    return (Void) -> this.setDialogOpen(true);
  }

  @Override
  public Boolean getDialogIsOpen() {
    return this.isDialogOpen();
  }

  @Override
  public void setDialogIsOpen(Boolean conv_val_$) {
    this.setDialogOpen(conv_val_$);
  }
}
