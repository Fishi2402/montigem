package hardy;

//+ HWC-P        : extension for the product

public class AddressValidator extends AddressValidatorTOP {

    public boolean extraBuildingChecks(AddressBuilder o, ResultLog log) {
        return true;
    }

    public boolean extraConsistencyChecks(Address o, ResultLog log) {
        return true;
    }
}


