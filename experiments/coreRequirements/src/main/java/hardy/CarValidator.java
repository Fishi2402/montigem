package hardy;

//+ GEN-P        : generated for the product

import de.se_rwth.commons.logging.Log;

public class CarValidator {

  public boolean buildingCheck(CarBuilder o) {
    boolean result = true;
    if(!o.isPresentOwns){
      result = false;
      // todo: Fuer wen ist diese Meldung?
      // Sieht sie für den Zweck adäquat weiterverarbeitbar oder verstehbar aus?
      Log.error("Car can not be build since it has no owner");
    }
    result &= extraBuildingChecks(o);
    return result;
  }

  public boolean extraBuildingChecks(CarBuilder o) {
      return true;
  }

  public boolean consistencyCheck(Car o) {
    boolean result = true;
    result &= extraConsistencyChecks(o);
    return result;
  }

  protected boolean extraConsistencyChecks(Car o){
    return true;
  }
}


