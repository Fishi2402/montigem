/* (c) https://github.com/MontiCore/monticore */
package gui.dimension;

import demorec.DemoRecManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class DimensionCreate extends DimensionCreateTOP {

  @Override
  public Consumer<Void> createDimension() {
    return $ -> {
      this.getDimension().build();

      this.setDimension(DemoRecManager.dimensionBuilder());
    };
  }
}


