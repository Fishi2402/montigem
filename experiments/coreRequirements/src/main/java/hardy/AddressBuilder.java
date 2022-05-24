package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.ValidationMessage;

import java.util.List;
import java.util.Optional;

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

    public Optional<Address> build() {
        if (!buildFinished) {
            if (validator.buildingCheck(this)) {
                FlensburgManager.addAddress(this);
                notifyObservers();      //+ an initial notification
                buildFinished = true;
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    public List<ValidationMessage> getValidatorMessages() {
        return validator.getValidatorMessages();
    }

    public boolean isBuildable() {
        return validator.buildingCheck(this);
    }
}


