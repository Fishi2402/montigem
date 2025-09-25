package example_input;

import domain.Person;
import domain.Role;

import java.util.ArrayList;
import java.util.List;

public class GemRadioInputExample extends GemRadioInputExampleTOP {

  @Override
  public List<Role> getRoles() {
    List<Role> roles = new ArrayList<Role>();
    for (Role role : Role.values()) {
      roles.add(role);
    }
    return roles;
  }

  @Override
  public String getPersonRoleInfo(Person person) {
    return person.getName() + " has role " + person.getRole().toString();
  }

  @Override
  public String getGroupName(Person person) {
    return "roles" + person.getName();
  }
}
