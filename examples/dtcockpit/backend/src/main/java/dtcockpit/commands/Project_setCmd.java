/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;


import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.Project;
import dtcockpit.montiverse.MontiVerseLite;
import umlp.backendrte.command.StatusResult;

import java.io.IOException;
import java.util.Optional;

public  class Project_setCmd extends Project_setCmdTOP  {


  @Override
  public StatusResult doAction() {
    StatusResult result = super.doAction();
    Project project = DtcockpitManager.getProjectSure(getGemId());
    MontiVerseLite.getInstance().createPipeline(project);
    return result;
  }

  public Project_setCmd(long gemId, Optional<String> cmd) {
    super(gemId, cmd);
  }
}


