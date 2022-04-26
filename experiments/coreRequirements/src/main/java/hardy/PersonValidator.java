package hardy;

//+ HWC-P        : extension for the product

/*+ PersonValidator checks a Person object for Validity:
    it can be called on objects in building or on Objects anywhere around
        
*/
public class PersonValidator extends PersonValidatorTOP {

    public boolean extraBuildingChecks(PersonBuilder o, ResultLog log) {
        if (o.getAge() >= 18) {
            return true;
        } else {
            // short notice, e.g. fürs Webformular
            log.add("Alter unter 18");
            return false;
        }
    }

    public boolean extraConsistencyChecks(Person o, ResultLog log) {
        if (o.getAge() >= 18) {
            return true;
        } else {
            // Aussagekräftigere Fehlermeldung
            log.add("%09d: Alter %d bei Person %s unter 18", o.getUmlpId(), o.getAge(), o.getNickname());
            return false;
        }
    }
}


