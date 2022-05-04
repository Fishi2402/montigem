package hardy;

//+ GEN-P        : generated for the product

// TODO: siehe Klasse PersonValidatorTOP
public abstract class HumanValidatorTOP {

    ResultLog log;

    public boolean buildingCheck(HumanBuilder o) {
      boolean result = true;
      result &= extraBuildingChecks( o, log);
      return result;
    }

    public boolean extraBuildingChecks(HumanBuilder o, ResultLog log) {
        return true;    
    }
}


