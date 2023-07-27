/* (c) https://github.com/MontiCore/monticore */
package hardy.flensburg;

// HWC-P

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CityInfo extends CityInfoTOP {

  @Override
  public int getNumberOfCities() {
    long count = 0L;
    Set<String> uniqueValues = new HashSet<>();
    for (Address address : FlensburgManager
        .getAddressList()) {
      String city = address.getCity();
      if (uniqueValues.add(city)) {
        count++;
      }
    }
    return (int) count;
  }

  @Override
  public int getTotalNumberOfCars() {
    return FlensburgManager.getCarList().size();
  }

  @Override
  public String getCityWithMostCars() {
    Map<String, Integer> cityToCars = new HashMap<>();
    List<Person> persons = FlensburgManager.getPersonList();

    for (Person person : persons) {
      int curOwns = person.sizeOwnss();
      if (person.checkMainResidence()) {
        String curCity = person.getMainResidence().getCity();
        cityToCars.put(curCity, curOwns + cityToCars.getOrDefault(curCity, 0));
      } else {
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
    Person result = null;
    int max = 0;
    for (Person person : FlensburgManager.getPersonList()) {
      if (person.sizeOwnss() > max) {
        max = person.sizeOwnss();
        result = person;
      }
    }
    return result;
  }

  @Override
  public int getAverageNumberOfCars() {
    return FlensburgManager.getCarList().size() / FlensburgManager.getPersonList().size();
  }

  @Override
  public  int sizeCities() {
    return getNumberOfCities();
  }

  @Override
  public List<String> getCities() {
    List<String> list = new ArrayList<>();
    for (Address address : FlensburgManager
        .getAddressList()) {
      String city = address.getCity();
      list.add(city);
    }
    return list;
  }
}
