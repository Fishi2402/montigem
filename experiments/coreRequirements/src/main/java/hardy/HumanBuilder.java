package hardy;

//+ GEN-P        : generated for the product

import java.util.Optional;

public class HumanBuilder extends Human {
    transient protected boolean buildFinished = false;
    transient protected boolean isAgePresent = false;

    // Bei der Deserialiserung kann davon ausgegangen werden, dass alle Objekte gebaut sind, und das
    // FlensburgManager#addX nicht vom Builder aufgerufen werden muss. Entweder das Objekt wird von Client geladen,
    // dann ist es bereits beim Server registiert, oder es wird am Ende der Erstellung and den Server gesendet, und
    // der X_create Command ruft danach FlensburgManager#addX auf.
    protected boolean INTERNAL_deser_mode = false;

    public void setINTERNAL_deser_mode(boolean value){
      this.INTERNAL_deser_mode = value;
    }

    public HumanBuilder(long gemId) {
       this.gemId = gemId;
    }

    public HumanBuilder age(int age) {
        this.age = age;
        isAgePresent = true;
        return this;
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    public Optional<Human> build() {
        if (!buildFinished) {
            if (isBuildable()) {
                if(!INTERNAL_deser_mode) {
                  FlensburgManager.addHuman(this);
                }
                humanObservers.forEach(obs -> obs.notifyBuild(this));
                buildFinished = true;
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    private boolean isBuildable() {
        // & anstelle von && damit immer alle Fehlermeldungen reported werden
        return checkAgePresent()
            & checkAge();
    }

    public boolean checkAgePresent() {
        if(!isAgePresent){
            HumanErrorCollector.reportAgeInvalid(this);
        }
        return isAgePresent;
    }
}


