package hardy;

//+ GEN-P        : generated for the product

import umlp.rte.ValidationMessage;

import java.util.List;
import java.util.Optional;

public class HumanBuilder extends Human {

    protected boolean buildFinished = false;

    protected boolean agePresent = false;

    HumanValidator validator;

    public HumanBuilder(long gemId, HumanValidator validator) {
       this.gemId = gemId;
       this.validator = validator;
       init();
    }

    public HumanBuilder age(int age) {
        this.age = age;
        this.agePresent = true;
        return this;
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    public boolean isAgePresent() {
        return agePresent;
    }

    public Optional<Human> build() {
        if (!buildFinished) {
            if (validator.buildingCheck(this)) {
                FlensburgManager.addHuman(this);
                notifyObservers();      //+ an initial notification
                buildFinished = true;
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


