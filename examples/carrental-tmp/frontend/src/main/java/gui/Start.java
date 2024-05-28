/* (c) https://github.com/MontiCore/monticore */
package gui;

import carrental.CarrentalManager;
import de.monticore.guidsl.types.Event;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class Start extends StartTOP {

  @Override
  public Consumer<Void> addCar() {
    // Add a car
    return $ -> CarrentalManager.carBuilder()
        .model("Hello there")
        .name("H")
        .make("X")
        .build();
  }

}
