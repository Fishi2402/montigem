package hardy;

//+ GEN-P        : generated for the product

import de.se_rwth.commons.logging.Log;

public class HumanValidator {

    public boolean buildingCheck(HumanBuilder o) {
        boolean result = true;
        if (!o.isAgePresent()) {
            result = false;
            Log.error("age not defined");
        }
        result &= extraBuildingChecks(o);
        return result;
    }

    public boolean extraBuildingChecks(HumanBuilder o) {
        return true;
    }

    public boolean consistencyCheck(Human o) {
        boolean result = true;
        result &= extraConsistencyChecks(o);
        return result;
    }

    protected boolean extraConsistencyChecks(Human o){
        return true;
    }
}


