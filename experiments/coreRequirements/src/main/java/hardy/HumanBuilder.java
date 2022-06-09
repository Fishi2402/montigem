package hardy;

//+ GEN-P        : generated for the product

import java.util.Optional;

public class HumanBuilder extends Human {
    transient protected boolean buildFinished = false;
    transient protected boolean isAgePresent = false;

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
                FlensburgManager.addHuman(this);
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


