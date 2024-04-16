/* (c) https://github.com/MontiCore/monticore */
package carrental;

public class Car extends CarTOP {
  @Override
  public boolean isAvailable() {
    return false;
//     TODO:
//    return this.getRentableStatus() == RentableStatus.AVAILABLE;
  }

  @Override
  public float getMileage() {
    return 0;
  }
}
