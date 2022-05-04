package hardy;

//+ GEN-P        : generated for the product

// TODO: siehe Klasse PersonValidatorTOP
public abstract class AddressValidatorTOP {

    ResultLog log;

    public boolean buildingCheck(AddressBuilder o) {
      boolean result = true;
      result &= extraBuildingChecks( o, log);
      return result;
    }

    public boolean extraBuildingChecks(AddressBuilder o, ResultLog log) {
        return true;    
    }
}


