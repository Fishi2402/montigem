package hardy;

//+ GEN-P        : generated for the product

import java.util.*;

/*+ PersonValidator checks a Person object for Validity: 
    it can be called on objects in building or on Objects anywhere around
        
*/
public abstract class PersonValidatorTOP {

    ResultLog log;     //todo: effizient gestalten, ggf. über mehrere Builder hinweg

    //+buildingCheck wird benutzt vom Builder 
    // zB via einem Webformular um Nutzer zu 
    // sagen: es ist was zu tun
    public boolean buildingCheck(PersonBuilder o) {
      boolean result = true;
      if (!getagePresent()) {
        result = false;
        // short notice, e.g. fürs Webformular
        log.add("age not defined");
      }
      // todo: analog für beide anderen attribute
      result &= extraBuildingChecks( o, log);
      return result;
    }

    // einfach mal einen Extension point hier reingelegt
    public boolean extraBuildingChecks(PersonBuilder o, ResultLog log) {
        return true;    
    }

    //+consistencyCheck kann zwischendurch benutzt werden 
    // zB. wenn das Risko besteht, dass Inkonsistenz herrscht 
    // Was tun wir dann? Fehler melden? Log, Error, Robustes Weitermachen?

    // todo: analog die schärfere Konsistenz variante:

}


