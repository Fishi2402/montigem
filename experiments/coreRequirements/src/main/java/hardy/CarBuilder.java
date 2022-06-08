package hardy;

//+ GEN-P        : generated for the product

import java.util.Optional;

public class CarBuilder extends Car {

    protected boolean buildFinished = false;

    // Die Assoziation owns ist im Builder bidirektional, damit sichergestellt werden kann,
    // dass jedes Car eine Person hat.
    // TODO: owns klingt als ob die Person eigentum vom Car ist.
    // Besser wäre ownedBy, automatische Übersetzung klappt nicht. Ggf. postfix _inverted hinzufügen
    Person owns;

    // TODO AHe: für mainResidence das selbe

    public CarBuilder(long gemId) {
       this.gemId = gemId;
       init();
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    public CarBuilder owns(Person p){
        this.owns = p;
        return this;
    }

    // Dieser check ist nicht in Car verfügbar, da owns nur vor dem Build bidirektional ist
    // TODO: muss auch nach build() überprüfbar sein!
    public boolean checkOwns(){
        boolean res = true;
        if(owns == null){
            res = false;
            CarErrorCollector.reportOwnsInvalid(this);
        }

        return res;
    }

    @Override
    public Optional<Car> build() {
        if (!buildFinished) {
            if (buildChecks()) {
                FlensburgManager.addCar(this);
                notifyObservers();      //+ an initial notification
                buildFinished = true;
                owns.addOwns(this);
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    public boolean buildChecks(){
        return checkOwns();
    }
}


