package hardy;

//+ GEN-P        : generated for the product

// TODO: ähnlich wie PersonBuilder aufbauen
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
    public Car build() {
        if (!buildFinished) {
            if (validator.buildingCheck(this)) {
                FlensburgManager.addCar(this);
                notifyObservers();      //+ an initial notification
                buildFinished = true;
                owns.addOwns(this);
                return this;
            }
        }
        // todo CKi, AHe: Generell können hier eine Exceptions geworfen werfen. Keine !
        throw new IllegalStateException("0xU1234: Car cannot be built.");
    }

    public boolean isBuildable() {
        return validator.buildingCheck(this);
    }
}


