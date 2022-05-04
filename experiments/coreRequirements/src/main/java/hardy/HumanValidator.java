package hardy;

//+ HWC-P        : extension for the product

public class HumanValidator extends HumanValidatorTOP {

    public boolean extraBuildingChecks(HumanBuilder o, ResultLog log) {
        return true;
    }

    public boolean extraConsistencyChecks(Human o, ResultLog log) {
        return true;
    }
}


