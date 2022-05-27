package hardy;

//+ GEN-P        : generated for the product

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import umlp.rte.ValidationMessage;

import java.util.List;

public class CarValidator {

  protected List<ValidationMessage> validationMessages = Lists.newLinkedList();

  public boolean buildingCheck(CarBuilder o) {
    boolean result = true;
    if(!o.isPresentOwns){
      result = false;
      validationMessages.add(ValidationMessage.create("Car can not be build since it has no owner"));
    }
    result &= extraBuildingChecks(o);
    return result;
  }

  public boolean extraBuildingChecks(CarBuilder o) {
      return true;
  }

  public boolean consistencyCheck(Car o) {
    boolean result = true;
    result &= extraConsistencyChecks(o);
    return result;
  }

  protected boolean extraConsistencyChecks(Car o){
    return true;
  }

    public List<ValidationMessage> getValidationMessages() {
      return validationMessages;
    }
}


