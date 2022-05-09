package hardy;

//+ GEN-P        : generated for the product

// TODO: ähnlich wie PersonBuilder aufbauen
public class AddressBuilder extends Address {

    protected boolean buildFinished = false;

    AddressValidator validator;

    public AddressBuilder(long gemId, AddressValidator validator) {
       this.gemId = gemId;
       this.validator = validator;
       init();
    }

    public boolean isBuildFinished() {
        return buildFinished;
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
        // todo: discuss: ist das Werfen einer Exception hier das richtige? Log.error?
        // todo: wir brauchen offensichtlich eine saubere Planung unser Fehlerstrategie
        throw new DataConsistencyViolationException("0xU1234: Address cannot be built.");
    }

    public boolean isBuildable() {
        return validator.buildingCheck(this);
    }
}


