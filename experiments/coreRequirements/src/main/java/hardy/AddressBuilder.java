package hardy;

//+ GEN-P        : generated for the product

import java.util.Collections;
import java.util.Optional;

public class AddressBuilder extends Address {

    protected boolean buildFinished = false;
    protected boolean isPresentCity = false;

    public AddressBuilder(long gemId) {
       this.gemId = gemId;
       this.visitors = FlensburgManager.createVisitorsBuilder(this, FlensburgManager.getFreeGemListId(), Collections.emptyList());
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
                visitors.build();
                FlensburgManager.addAddress(this);
                addressObservers.forEach(obs -> obs.notifyBuild(this));
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


