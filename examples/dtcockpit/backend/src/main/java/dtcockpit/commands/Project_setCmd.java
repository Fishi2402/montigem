/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;


import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import umlp.backendrte.command.StatusResult;

import java.io.IOException;
import java.util.Optional;

public  class Project_setCmd extends Project_setCmdTOP  {


  @Override
  public StatusResult doAction() {
    StatusResult result = super.doAction();
    try {
      Log.info("Execute command " + this.getCmd(), getClass().getName());
      ProcessBuilder processBuilder = new ProcessBuilder(this.getCmd().split(" "));
      processBuilder.start();
    } catch (IOException e) {
      Log.info("Error during executing command \n" + this.getCmd(), getClass().getName());
    }
    return result;
  }

  public Project_setCmd(long gemId, Optional<String> cmd) {
    super(gemId, cmd);
  }
}


