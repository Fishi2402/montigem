package hardy;

//+ GEN-P

import umlp.rte.Gem;

import java.util.Objects;
import java.util.Observable;

// Berechnete Klasse!
public class CityInfo extends Observable {
  // AHe: Mmn macht eine gemId in berechneten Klassen keinen Sinn, da diese nicht persistiert werden und eine gemId benutzt wird um eine bestimmte Instanz aus dem Speicher/der Db/... zu laden.

  protected int numberOfCities;
  protected int totalNumberOfCars;
  protected String cityWithMostCars;
  protected Gem<Person> personWithMostCars;
  protected int averageNumberOfCars;

  protected CityInfo(){
    init();
  }

  protected void init() {
    numberOfCities = 0;
    totalNumberOfCars = 0;
    cityWithMostCars = "";
    personWithMostCars = null;
    averageNumberOfCars = 0;
  }


  // Berechnete Klasse, daher sind alle setter protected. Nur der Builder darf sie ändern.
  // Alternative: 1 Konstruktor der alle Felder fordert, alle Felder final
  protected void setNumberOfCities(int number){
    this.numberOfCities = numberOfCities;
  }

  protected void setTotalNumberOfCars(int totalNumberOfCars){
    this.totalNumberOfCars = totalNumberOfCars;
  }

  protected void setCityWithMostCars(String cityWithMostCars){
    this.cityWithMostCars = Objects.requireNonNull(cityWithMostCars);
  }

  protected void setPersonWithMostCars(Person personWithMostCars){
    if (this.personWithMostCars == null) {
      this.personWithMostCars = new Gem<>(Objects.requireNonNull(personWithMostCars));
    }else{
      this.personWithMostCars.set(Objects.requireNonNull(personWithMostCars));
    }
  }

  protected void setAverageNumberOfCars(int averageNumberOfCars){
    this.averageNumberOfCars = averageNumberOfCars;
  }

  // Auch in einer berechneten Klasse sind die Getter public
  public int getNumberOfCities(){
    return numberOfCities;
  }

  public int getTotalNumberOfCars(){
    return totalNumberOfCars;
  }

  public String getCityWithMostCars(){
    return cityWithMostCars;
  }

  public boolean availablePersonWithMostCars(){
    if(personWithMostCars == null){
      return false;
    } else {
      return personWithMostCars.isPresent();
    }
  }

  public Person getPersonWithMostCars(){
    return personWithMostCars.get();
  }

  public int getAverageNumberOfCars(){
    return averageNumberOfCars;
  }
}
