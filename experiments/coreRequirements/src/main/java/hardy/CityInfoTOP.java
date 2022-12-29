/* (c) https://github.com/MontiCore/monticore */
package hardy;

//+ GEN-P

import java.util.List;
import java.util.Observable;

// Berechnete Klasse!
public abstract class CityInfoTOP extends Observable {
  // AHe: Mmn macht eine gemId in berechneten Klassen keinen Sinn, da diese nicht persistiert werden und eine gemId benutzt wird um eine bestimmte Instanz aus dem Speicher/der Db/... zu laden.
  
  protected CityInfoTOP() {
    init();
  }
  
  protected void init() {
  
  }
  
  public abstract int getNumberOfCities();
  
  public abstract int getTotalNumberOfCars();
  
  public abstract String getCityWithMostCars();
  
  public abstract Person getPersonWithMostCars();
  
  public abstract int getAverageNumberOfCars();
  
  // Berechnete association cities
  public abstract int sizeCities();
  
  public abstract List<String> getCities();
  
  // + weitere lesende Listen Methoden
  
  // Da cities berechnet ist, gibt es keine Methoden zum Manipulieren
}
