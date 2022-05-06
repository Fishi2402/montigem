package hardy;

//+ GEN-P        : generated for the product

public class HumanValidator {

    ResultLog log = new ResultLog();

    public boolean buildingCheck(HumanBuilder o) {
        boolean result = true;
        if (!o.isAgePresent()) {
            result = false;
            log.add("age not defined");
        }
        result &= extraBuildingChecks(o, log);
        return result;
    }

    public boolean extraBuildingChecks(HumanBuilder o, ResultLog log) {
        return true;
    }

    public boolean consistencyCheck(Human o, ResultLog log) {
        boolean result = true;
        result &= extraConsistencyChecks(o, log);
        return result;
    }

    protected boolean extraConsistencyChecks(Human o, ResultLog log){
        return true;
    }
}


