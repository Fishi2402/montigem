package hardy;

//+ GEN-P        : generated for the product

// TODO: ähnlich wie PersonBuilder aufbauen
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

    public Human build() {
        if (!buildFinished) {
            if (validator.buildingCheck(this)) {
                FlensburgManager.addHuman(this);
                notifyObservers();      //+ an initial notification
                buildFinished = true;
                return this;
            }
        }
        throw new DataConsistencyViolationException("0xU1234: Human cannot be built.");
    }

    public boolean isBuildable() {
        return validator.buildingCheck(this);
    }
}


