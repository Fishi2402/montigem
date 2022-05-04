package hardy;

//+ HWC-P        : extension for the product

public class CarValidator extends CarValidatorTOP {

    public boolean extraBuildingChecks(CarBuilder o, ResultLog log) {
        return true;
    }

    public boolean extraConsistencyChecks(Car o, ResultLog log) {
        return true;
    }
}


