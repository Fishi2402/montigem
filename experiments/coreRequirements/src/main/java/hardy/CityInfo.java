package hardy;

// HWC-P

import java.util.*;
import java.util.stream.Collectors;

public class CityInfo extends CityInfoTOP{

  @Override
  public int getNumberOfCities() {
    return (int) FlensburgManager
        .getAddressList()
        .stream()
        .map(Address::getCity)
        .distinct()
        .count();
  }

  @Override
  public int getTotalNumberOfCars() {
    return FlensburgManager.getCarList().size();
  }

  @Override
  public String getCityWithMostCars() {
    Map<String, Integer> cityToCars = new HashMap<>();
    Collection<Person> persons = FlensburgManager.getPersonList();

    for (Person person : persons) {
      int curOwns = person.sizeOwnss();
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

    return cityWithMostCars;
  }

  @Override
  public Person getPersonWithMostCars() {
    return FlensburgManager
        .getPersonList()
        .stream()
        .max(Comparator.comparingInt(Person::sizeOwnss))
        .get();
  }

  @Override
  public int getAverageNumberOfCars() {
    return FlensburgManager.getCarList().size()/FlensburgManager.getPersonList().size();
  }

  @Override
  public int sizeCities() {
    return getNumberOfCities();
  }

  @Override
  public List<String> getCities() {
    return FlensburgManager
        .getAddressList()
        .stream()
        .map(Address::getCity)
        .collect(Collectors.toList());
  }
}
