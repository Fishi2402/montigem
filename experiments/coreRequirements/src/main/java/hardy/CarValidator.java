package hardy;

//+ GEN-P        : generated for the product

public class CarValidator {
  ResultLog log = new ResultLog();

  public boolean buildingCheck(CarBuilder o) {
    boolean result = true;
    if(!o.isPresentOwns){
      result = false;
      log.add("Car can not be build since it has no owner");
    }
    result &= extraBuildingChecks( o, log);
    return result;
  }

  public boolean extraBuildingChecks(CarBuilder o, ResultLog log) {
      return true;
  }

  public boolean consistencyCheck(Car o, ResultLog log) {
    boolean result = true;
    result &= extraConsistencyChecks(o, log);
    return result;
  }

  protected boolean extraConsistencyChecks(Car o, ResultLog log){
    return true;
  }
}


