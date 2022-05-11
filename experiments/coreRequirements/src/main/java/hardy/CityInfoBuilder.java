package hardy;
//+ GEN-P

import umlp.rte.Gem;

public class CityInfoBuilder extends CityInfo {
  protected CityInfoValidator validator;

  protected boolean buildFinished = false;
  protected boolean isPresentNumberOfCities;
  protected boolean isPresentTotalNumberOfCars;
  protected boolean isPresentCityWithMostCars;
  protected boolean isPresentPersonWithMostCars;
  protected boolean isPresentAverageNumberOfCars;

  public CityInfoBuilder(CityInfoValidator theCityInfoValidator) {
    validator = theCityInfoValidator;
  }

  public CityInfoBuilder numberOfCities(int numberOfCities){
    this.numberOfCities = numberOfCities;
    isPresentNumberOfCities = true;
    return this;
  }

  public CityInfoBuilder totalNumberOfCars(int totalNumberOfCars){
    this.totalNumberOfCars = totalNumberOfCars;
    isPresentTotalNumberOfCars = true;
    return this;
  }

  public CityInfoBuilder cityWithMostCars(String cityWithMostCars){
    this.cityWithMostCars = cityWithMostCars;
    isPresentCityWithMostCars = true;
    return this;
  }

  public CityInfoBuilder personWithMostCars(Gem<Person> personWithMostCars){
    this.personWithMostCars = personWithMostCars;
    isPresentPersonWithMostCars = true;
    return this;
  }

  public CityInfoBuilder averageNumberOfCars(int averageNumberOfCars){
    this.averageNumberOfCars = averageNumberOfCars;
    isPresentAverageNumberOfCars = true;
    return this;
  }

  public CityInfo build(){
    if(!buildFinished){
      if (validator.buildingCheck(this)) {
        // Berechnete Klassen lassen sich nicht zum Manager hinzufügen, daher wird das Ergebnis hier nur zurückgegeben
        notifyObservers();
        buildFinished = true;
        return this;
      }
    }
    throw new IllegalStateException("0xU1234: CityInfo cannot be built.");
  }
}
