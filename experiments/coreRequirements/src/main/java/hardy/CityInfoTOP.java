package hardy;

//+ GEN-P

import java.util.Observable;

// Berechnete Klasse!
public abstract class CityInfoTOP extends Observable {
  // AHe: Mmn macht eine gemId in berechneten Klassen keinen Sinn, da diese nicht persistiert werden und eine gemId benutzt wird um eine bestimmte Instanz aus dem Speicher/der Db/... zu laden.

  protected CityInfoTOP(){
    init();
  }

  protected void init(){

  }


  public abstract int getNumberOfCities();

  public abstract int getTotalNumberOfCars();

  public abstract String getCityWithMostCars();

  public abstract Person getPersonWithMostCars();

  public abstract int getAverageNumberOfCars();
}
