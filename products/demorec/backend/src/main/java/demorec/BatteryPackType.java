package demorec;

public class BatteryPackType extends BatteryPackTypeTOP {

  public String getName() {
    String calcName = "";
    if(this.getCarYear().isPresent()) {
      calcName += getCarYear().get();
    }
    if(this.getCarBrand().isPresent()) {
      calcName += getCarBrand().get();
    }
    if(this.getCarModel().isPresent()) {
      calcName += getCarModel().get();
    }
    return calcName;
  }
}
