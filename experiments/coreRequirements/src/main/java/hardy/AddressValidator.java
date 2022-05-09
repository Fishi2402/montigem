package hardy;

//+ GEN-P        : generated for the product

// TODO: siehe Klasse PersonValidatorTOP
public class AddressValidator {

  ResultLog log;

  public boolean buildingCheck(AddressBuilder o) {
    boolean result = true;
    result &= o.isPresentCity;
    result &= extraBuildingChecks( o, log);
    return result;
  }

  public boolean extraBuildingChecks(AddressBuilder o, ResultLog log) {
      return true;
  }

  public boolean consistencyCheck(Address o, ResultLog log) {
    boolean result = true;
    result &= extraConsistencyChecks(o, log);
    return result;
  }

  protected boolean extraConsistencyChecks(Address o, ResultLog log){
    return true;
  }
}


