package hardy;

//+ GEN-P        : generated for the product

import java.util.Optional;

public class AddressBuilder extends Address {

    protected boolean buildFinished = false;
    // Attribute wie isPresentCity sind nicht notwendig, da checkCity überprüft, ob city richtig
    // gesetzt wurde

    public AddressBuilder(long gemId) {
       this.gemId = gemId;
       init();
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    public AddressBuilder city(String city){
        this.city = city;
        return this;
    }

    public Optional<Address> build() {
        if (!buildFinished) {
            if(buildChecks()) {
                FlensburgManager.addAddress(this);
                notifyObservers();      //+ an initial notification
                buildFinished = true;
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    private boolean buildChecks() {
        return checkCity();
    }
}


