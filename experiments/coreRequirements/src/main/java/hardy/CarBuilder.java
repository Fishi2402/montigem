package hardy;

//+ GEN-P        : generated for the product

import java.util.Optional;

public class CarBuilder extends Car {

    protected boolean buildFinished = false;

    // Bei der Deserialiserung kann davon ausgegangen werden, dass alle Objekte gebaut sind, und das
    // FlensburgManager#addX nicht vom Builder aufgerufen werden muss. Entweder das Objekt wird von Client geladen,
    // dann ist es bereits beim Server registiert, oder es wird am Ende der Erstellung and den Server gesendet, und
    // der X_create Command ruft danach FlensburgManager#addX auf.
    protected boolean INTERNAL_deser_mode = false;

    public void setINTERNAL_deser_mode(boolean value){
      this.INTERNAL_deser_mode = value;
    }

    // Die Assoziation owns ist im Builder bidirektional, damit sichergestellt werden kann,
    // dass jedes Car eine Person hat.
    // TODO: owns klingt als ob die Person eigentum vom Car ist.
    // Besser wäre ownedBy, automatische Übersetzung klappt nicht. Ggf. postfix _inverted hinzufügen
    transient Person owns;

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
            if (isBuildable()) {
                if(!INTERNAL_deser_mode) {
                  FlensburgManager.addCar(this);
                }
                carObservers.forEach(obs -> obs.notifyBuild(this)); //+ an initial notification
                buildFinished = true;
                owns.addOwns(this);
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    public boolean isBuildable(){
        return checkOwns();
    }
}


