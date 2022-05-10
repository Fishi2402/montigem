package hardy;

//+ GEN-P        : generated for the product

import java.util.Set;

// TODO: ähnlich wie PersonBuilder aufbauen
public class CarBuilder extends Car {

    protected boolean buildFinished = false;

    CarValidator validator;

    public CarBuilder(long gemId, CarValidator validator) {
       this.gemId = gemId;
       this.validator = validator;
       init();
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    public Car build() {
        if (!buildFinished) {
            if (validator.buildingCheck(this)) {
                FlensburgManager.addCar(this);
                notifyObservers();      //+ an initial notification
                buildFinished = true;
                return this;
            }
        }
        throw new IllegalStateException("0xU1234: Car cannot be built.");
    }

    public boolean isBuildable() {
        return validator.buildingCheck(this);
    }
}


