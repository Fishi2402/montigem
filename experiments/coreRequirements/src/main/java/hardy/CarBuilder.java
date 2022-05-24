package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.ValidationMessage;

import java.util.List;
import java.util.Optional;

public class CarBuilder extends Car {

    protected boolean buildFinished = false;

    // Die Assoziation owns ist im Builder bidirektional, damit der CarValidator sicherstellen kann,
    // dass jedes Car eine Person hat.
    // TODO: owns klingt als ob die Person eigentum vom Car ist.
    // Besser wäre ownedBy, automatische Übersetzung klappt nicht. Ggf. postfix _inverted hinzufügen
    Person owns;
    protected boolean isPresentOwns = false;

    // TODO AHe: für mainResidence das selbe

    CarValidator validator;

    public CarBuilder(long gemId, CarValidator validator) {
       this.gemId = gemId;
       this.validator = validator;
       init();
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    public CarBuilder owns(Person p){
        this.owns = p;
        isPresentOwns = true;
        return this;
    }

    @Override
    public Optional<Car> build() {
        if (!buildFinished) {
            if (validator.buildingCheck(this)) {
                FlensburgManager.addCar(this);
                notifyObservers();      //+ an initial notification
                buildFinished = true;
                owns.addOwns(this);
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    public boolean isBuildable() {
        return validator.buildingCheck(this);
    }

    public List<ValidationMessage> getValidationMessages() {
        return validator.getValidationMessages();
    }
}


