/* (c) https://github.com/MontiCore/monticore */
package sehub;

import umlp.backendrte.command.Command;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

public class IssueAccessPolicy extends IssueAccessPolicyTOP {
  @Override
  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  public boolean checkPermission(Command command) {
    // System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
    // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    System.err.println("I am checking Permission of Command + " + command.getCommandType());
    return true;
  }
}

