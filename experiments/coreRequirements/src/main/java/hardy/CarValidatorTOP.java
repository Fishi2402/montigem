package hardy;

//+ GEN-P        : generated for the product

// TODO: siehe Klasse PersonValidatorTOP
public abstract class CarValidatorTOP {

    ResultLog log;

    public boolean buildingCheck(CarBuilder o) {
      boolean result = true;
      result &= extraBuildingChecks( o, log);
      return result;
    }

    public boolean extraBuildingChecks(CarBuilder o, ResultLog log) {
        return true;    
    }
}


