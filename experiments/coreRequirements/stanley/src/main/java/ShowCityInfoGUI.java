package stanley.showcityinfo;

// GEN-T

/* This is the code that is generated for testing the GUI
   functionality within Java:
   It mimicks the GUI functionality as well as
   prepares an API where the test drive can mimick user inputs

The source for this class was (taken from models/NeuePersonAnlegen.gui):

page ShowCityInfo(CityInfo ci) { // derived CityInfo
  :Table(
    rows = List(
      Row("Number of cities", ci.numberOfCities),
      Row("Total number of cars", ci.totalNumberOfCars),
      Row("City with most cars", ci.cityWithMostCars),
      Row("Person with most cars", ci.personWithMostCars.nickname),
      Row("Average number of cars", ci.averageNumberOfCars)
    )
  )

  // list of cities
  :Text(value = "List of cities")
  iterate(cityName : ci.cities) {
    :Text(value = cityName)
  }
}

*/

import hardy.CityInfo;
import hardy.FlensburgManager;

import java.util.List;

/********************************************************/

public class ShowCityInfoGUI {

  // Person does not exist in the beginning, use builder instead
  protected CityInfo ci;

  /* Z1
   * Constructor: uses the same parameters as the GUI-page,
   * The constructor call mimics the start of the Window: it is opened,
   * its values are set etc.
   */
  public ShowCityInfoGUI() {
    this.ci = FlensburgManager.getCityInfo();
  }

  // **************************** What is shown? ********************
  // Values in the compartments are simulated via getter's
  // If values are not present, GUI does not change or react

  public CityInfo getTable() {
    return ci;
  }

  public List<String> getCities() {
    return ci.getCities();
  }

}
