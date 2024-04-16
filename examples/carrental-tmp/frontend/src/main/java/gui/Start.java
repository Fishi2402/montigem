/* (c) https://github.com/MontiCore/monticore */
package gui;

import carrental.CarrentalManager;
import umlp.backendrte.common.GemList;
import umlp.jsweet.extension.annotation.Component;

@Component
public class Start extends StartTOP {
  public Start(GemList cars) {
    super(cars);
  }

  @Override
  public void addCar(Object event) {
    // Add a car
    CarrentalManager.carBuilder()
        .model("Hello there")
        .name("H")
        .make("X")
        .build();
  }
}
