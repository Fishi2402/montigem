package hardy;

//+ HWC-P        : extension for the product

public class CarValidator extends CarValidatorTOP {

    @Override
    public boolean extraBuildingChecks(CarBuilder o, ResultLog log) {
        return true;
    }

    @Override
    public boolean extraConsistencyChecks(Car o, ResultLog log) {
        return true;
    }
}


