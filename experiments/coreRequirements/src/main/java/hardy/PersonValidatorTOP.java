package hardy;

//+ GEN-P        : generated for the product

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import umlp.rte.ValidationMessage;

import java.util.List;

/*+ PersonValidator checks a Person object for Validity:
    it can be called on objects in building or on Objects anywhere around
*/
public abstract class PersonValidatorTOP {

    protected List<ValidationMessage> validationMessages = Lists.newLinkedList();

    //+buildingCheck wird benutzt vom Builder 
    // zB via einem Webformular um Nutzer zu 
    // sagen: es ist was zu tun
    public boolean buildingCheck(PersonBuilder o) {
        validationMessages.clear();
        boolean result = true;
        if (!o.isAgePresent()) {
            result = false;
            // short notice, e.g. fürs Webformular
            Log.error("age not defined");
            validationMessages.add(ValidationMessage.create("age not defined"));
        }
        if (!o.isNicknamePresent()) {
            result = false;
            // short notice, e.g. fürs Webformular
            Log.error("nickname not defined");
            validationMessages.add(ValidationMessage.create("nickname not defined"));
        }

        if (!o.isOwnsPresent()) // todo
            // todo: analog für owns?

            result &= extraBuildingChecks(o);
        return result;
    }

    // einfach mal einen Extension point hier reingelegt
    public boolean extraBuildingChecks(PersonBuilder o) {
        return true;
    }

    //+consistencyCheck kann zwischendurch benutzt werden 
    // zB. wenn das Risko besteht, dass Inkonsistenz herrscht 
    // Was tun wir dann? Fehler melden? Log, Error, Robustes Weitermachen?

    public boolean consistencyCheck(Person p) {
        boolean result = true;
        // TODO AHe: what checks are needed here?
        result &= extraConsistencyChecks(p);
        return result;
    }

    public boolean extraConsistencyChecks(Person p) {
        return true;
    }

    // todo: analog die schärfere Konsistenz variante:

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }
}


