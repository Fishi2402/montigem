package hardy;

//+ GEN-P        : generated for the product

import com.google.common.collect.Lists;
import umlp.rte.ValidationMessage;

import java.util.LinkedHashMap;
import java.util.List;

// TODO: siehe Klasse PersonValidatorTOP
public class AddressValidator {

  protected List<ValidationMessage> validationMessages = Lists.newLinkedList();

  public boolean buildingCheck(AddressBuilder o) {
    boolean result = true;
    if (!o.isPresentCity) {
      result = false;
      validationMessages.add(ValidationMessage.create("city not defined"));
    }
    result &= extraBuildingChecks(o);
    return result;
  }

  public boolean extraBuildingChecks(AddressBuilder o) {
      return true;
  }

  public boolean consistencyCheck(Address o) {
    boolean result = true;
    result &= extraConsistencyChecks(o);
    return result;
  }

  protected boolean extraConsistencyChecks(Address o){
    return true;
  }

  public List<ValidationMessage> getValidatorMessages() {
    return validationMessages;
  }
}


