package gui.admin;

import kitchencompanion.KitchenCompanionManager;
import kitchencompanion.KitchenCompanionRole;
import kitchencompanion.User;
import mc.fenix.input.gemdropdowninputtypes.Option;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class UserForm extends UserFormTOP {

  @Override
  public List<Option> getRoleDropDownOptions() {
    List<Option> options = new ArrayList<>();
    for (KitchenCompanionRole r : KitchenCompanionRole.values()) {
      options.add(new Option(r.name()));
    }
    return options;
  }

  @Override
  public Consumer<Object> entryToLower(User u) {
    return $ -> {
      u.setEmail(u.getEmail().toLowerCase());
    };
  }

  // Validity Methods
  @Override
  public GemTextInputValidity getEmailValidity() {
    try {
      if (getU().isEmailValid()) {
        for (User user : KitchenCompanionManager.getUserList()) {
          if (user.getEmail().compareToIgnoreCase(getU().getEmail()) == 0 && user.getNickname().compareToIgnoreCase(getU().getNickname()) != 0) {
            return new GemTextInputValidity("Error", "A user with the same email already exists");
          }
        }
        return new GemTextInputValidity("Success", "Email is Valid");
      } else if (!getU().getEmail().equals("")) {
        return new GemTextInputValidity("Warning", "Email isn't Valid");
      } else {
        return new GemTextInputValidity("Error", "Field cannot be empty");
      }
    } catch (Exception e) {
      return new GemTextInputValidity("Unchecked", "havent checked yet");
    }
  }
}
