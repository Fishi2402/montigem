package hardy;

//+ GEN-P        : generated for the product

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import umlp.rte.ValidationMessage;

import java.util.List;

public class HumanValidator {

    protected List<ValidationMessage> validationMessages = Lists.newLinkedList();

    public boolean buildingCheck(HumanBuilder o) {
        boolean result = true;
        if (!o.isAgePresent()) {
            result = false;
            validationMessages.add(ValidationMessage.create("age not defined"));
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

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }
}


