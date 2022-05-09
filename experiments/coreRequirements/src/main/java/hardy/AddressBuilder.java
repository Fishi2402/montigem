package hardy;

//+ GEN-P        : generated for the product

// TODO: ähnlich wie PersonBuilder aufbauen
public class AddressBuilder extends Address {

    protected boolean buildFinished = false;
    protected boolean isPresentCity = false;

    AddressValidator validator;

    public AddressBuilder(long gemId, AddressValidator validator) {
       this.gemId = gemId;
       this.validator = validator;
       init();
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    public AddressBuilder city(String city){
        this.city = city;
        isPresentCity = true;
        return this;
    }

    public Address build() {
        if (!buildFinished) {
            if (validator.buildingCheck(this)) {
                FlensburgManager.addAddress(this);
                notifyObservers();      //+ an initial notification
                buildFinished = true;
                return this;
            }
        }
        throw new DataConsistencyViolationException("0xU1234: Address cannot be built.");
    }

    public boolean isBuildable() {
        return validator.buildingCheck(this);
    }
}


