package hardy;

//+ HWC-P        : extension for the product

public class AddressValidator extends AddressValidatorTOP {

    @Override
    public boolean extraBuildingChecks(AddressBuilder o, ResultLog log) {
        return true;
    }

    @Override
    public boolean extraConsistencyChecks(Address o, ResultLog log) {
        return true;
    }
}


