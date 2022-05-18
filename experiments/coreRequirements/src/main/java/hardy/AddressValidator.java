package hardy;

//+ GEN-P        : generated for the product

// TODO: siehe Klasse PersonValidatorTOP
public class AddressValidator {

  public boolean buildingCheck(AddressBuilder o) {
    boolean result = true;
    result &= o.isPresentCity;
    result &= extraBuildingChecks(o);
    return result;
  }

  public boolean extraBuildingChecks(AddressBuilder o) {
      return true;
  }

  public boolean consistencyCheck(Address o) {
    boolean result = true;
    result &= extraConsistencyChecks(o);
    return result;
  }

  protected boolean extraConsistencyChecks(Address o){
    return true;
  }
}


