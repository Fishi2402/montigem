package hardy;

//+ HWC-P        : extension for the product

import de.se_rwth.commons.logging.Log;
import umlp.rte.ValidationMessage;

/*+ PersonValidator checks a Person object for Validity:
    it can be called on objects in building or on Objects anywhere around
*/
public class PersonValidator extends PersonValidatorTOP {

    @Override
    public boolean extraBuildingChecks(PersonBuilder o) {
        if (o.getAge() >= 18) {
            return true;
        } else {
            // short notice, e.g. fürs Webformular
            validationMessages.add(ValidationMessage.create("Alter unter 18"));
            return false;
        }
    }

    @Override
    public boolean extraConsistencyChecks(Person o) {
        if (o.getAge() >= 18) {
            return true;
        } else {
            // Aussagekräftigere Fehlermeldung
            validationMessages.add(ValidationMessage.create(String.format("%09d: Alter %d bei Person %s unter 18",
                    o.getGemId(), o.getAge(), o.getNickname())));
            return false;
        }
    }
}


