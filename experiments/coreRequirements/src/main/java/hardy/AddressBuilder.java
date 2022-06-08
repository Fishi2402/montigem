package hardy;

//+ GEN-P        : generated for the product

import java.util.Optional;

public class AddressBuilder extends Address {

    protected boolean buildFinished = false;
    protected boolean isPresentCity = false;

    public AddressBuilder(long gemId) {
       this.gemId = gemId;
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
            if(isBuildable()) {
                FlensburgManager.addAddress(this);
                notifyObservers();      //+ an initial notification
                buildFinished = true;
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    private boolean isBuildable() {
        return checkCityPresent()
                & checkCity();
    }

    private boolean checkCityPresent() {
        if(!isPresentCity){
            AddressErrorCollector.reportCityInvalid(this);
        }
        return isPresentCity;
    }
}


