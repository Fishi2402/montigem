package hardy;
//+ GEN-P        : generated for the product

public class CityInfoValidator {
  ResultLog log = new ResultLog();

  public boolean buildingCheck(CityInfoBuilder o) {
    boolean result = true;
    if(!o.isPresentNumberOfCities){
      result = false;
      log.add("presentNumberOfCities not defined");
    }
    if(!o.isPresentTotalNumberOfCars){
      result = false;
      log.add("presentTotalNumberOfCars not defined");
    }
    if(!o.isPresentCityWithMostCars){
      result = false;
      log.add("presentCityWithMostCars not defined");
    }
    if(!o.isPresentPersonWithMostCars){
      result = false;
      log.add("presentPersonWithMostCars not defined");
    }
    if(!o.isPresentAverageNumberOfCars){
      result = false;
      log.add("presentAverageNumberOfCars not defined");
    }

    result &= extraBuildingChecks(o, log);
    return result;
  }

  public boolean extraBuildingChecks(CityInfoBuilder o, ResultLog log) {
    return true;
  }

  public boolean consistencyCheck(CityInfoBuilder o, ResultLog log) {
    boolean result = true;
    result &= extraConsistencyChecks(o, log);
    return result;
  }

  protected boolean extraConsistencyChecks(CityInfoBuilder o, ResultLog log){
    return true;
  }
}
