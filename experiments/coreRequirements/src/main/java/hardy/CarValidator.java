package hardy;

//+ GEN-P        : generated for the product

public class CarValidator {
  // todo CKn: Log ist eine global bekannte Klasse. Warum lässt sich die hier nicht verwenden?
  // Und warum muss diese Klasse ausgerechnet "ResultLog" heissen, obwohl sie kein log ist?
  ResultLog log = new ResultLog();

  public boolean buildingCheck(CarBuilder o) {
    boolean result = true;
    if(!o.isPresentOwns){
      result = false;
      // todo: Fuer wen ist diese Meldung?
      // Sieht sie für den Zweck adäquat weiterverarbeitbar oder verstehbar aus?
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


