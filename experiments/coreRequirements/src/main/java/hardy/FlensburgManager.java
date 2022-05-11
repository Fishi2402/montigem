package hardy;
//+ HWC-P

import umlp.rte.Gem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Used to implement calculated classes once, instead of one time per Manager implementation(FlensburgMock,...)
public abstract class FlensburgManager extends FlensburgManagerTOP{

  // Calculates CityInfo
  // This implementation is available in all FlensburgManager implementations like FlensburgMock and FlensburgManagerImpl
  @Override
  public CityInfo _doGetCityInfo() {
    // TODO AHe: what if there are no cities or persons?
    Person personWithMostCars = null;
    int personWithMostCarsValue = Integer.MIN_VALUE;
    Map<String, Integer> cityToCars = new HashMap<>();
    Collection<Person> persons = FlensburgManager.getPersonList();

    for (Person person : persons) {
      int curOwns = person.sizeOwnss();
      if(curOwns > personWithMostCarsValue){
        personWithMostCarsValue = curOwns;
        personWithMostCars = person;
      }

      if(person.availableMainResidence()) {
        String curCity = person.getMainResidence().getCity();
        cityToCars.put(curCity, curOwns + cityToCars.getOrDefault(curCity, 0));
      }else{
        // TODO: error handling
      }
    }

    String cityWithMostCars = null;
    int cityWithMostCarsValue = Integer.MIN_VALUE;

    for (Map.Entry<String, Integer> entry : cityToCars.entrySet()) {
      String city = entry.getKey();
      Integer cars = entry.getValue();
      if (cars > cityWithMostCarsValue) {
        cityWithMostCarsValue = cars;
        cityWithMostCars = city;
      }
    }

    int numberOfCars = FlensburgManager.getCarList().size();
    return _doCityInfo()
        .numberOfCities((int) FlensburgManager.getAddressList().stream().map(Address::getCity).distinct().count())
        .totalNumberOfCars(numberOfCars)
        .cityWithMostCars(cityWithMostCars)
        .personWithMostCars(new Gem<>(personWithMostCars))
        .averageNumberOfCars(numberOfCars/persons.size())
        .build();
  }
}
