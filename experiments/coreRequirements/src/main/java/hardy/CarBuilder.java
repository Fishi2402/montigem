/* (c) https://github.com/MontiCore/monticore */
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

    // TODO: alle Assoziationen bidirektional für Konsistenzchecks?

    public CarBuilder(long gemId) {
       this.gemId = gemId;
       init();
    }

    public boolean isBuildFinished() {
        return buildFinished;
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
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    public boolean isBuildable(){
        return true;
    }
}


