package hardy;

//+ GEN-P        : generated for the product

import java.util.Optional;

public class HumanBuilder extends Human {
    transient protected boolean buildFinished = false;
    // Attribute der Form isPresentX sind nur für primitive Typen notwendig, bei nicht-primitiven
    // kann checkX auf null überprüfen

    public HumanBuilder(long gemId) {
       this.gemId = gemId;
    }

    public HumanBuilder age(int age) {
        this.age = age;
        return this;
    }

    public boolean isBuildFinished() {
        return buildFinished;
    }

    public Optional<Human> build() {
        if (!buildFinished) {
            if (buildChecks()) {
                FlensburgManager.addHuman(this);
                humanObservers.forEach(obs -> obs.notifyBuild(this));
                buildFinished = true;
                return Optional.of(this);
            }
        }
        return Optional.empty();
    }

    private boolean buildChecks() {
        return checkAge();
    }
}


