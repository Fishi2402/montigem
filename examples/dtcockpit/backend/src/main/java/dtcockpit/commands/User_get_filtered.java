/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;

import dtcockpit.DtcockpitManager;
import umlp.backendrte.command.ListResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.ArrayList;
import java.util.List;

// TODO: Module System: User_create and LoginModuleUser_create have no relation at all
public class User_get_filtered extends User_get_filteredTOP {

  public User_get_filtered() {
    super();
  }

  public User_get_filtered(String filter) {
    super(filter);
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  @Override
  public ListResult<Long, List<Long>> doAction() {

    List<Long> validAccessIDs = new ArrayList<>();
    for (long id : DtcockpitManager.getUserList().getIDs()) {
      User_get atomicCmd = new User_get(id);
      atomicCmd.setParticipantId(this.getParticipantId());
      if (atomicCmd.doRun().isSuccessful()) {
        validAccessIDs.add(id);
      }
    }

    if (!validAccessIDs.isEmpty()) {
      return ListResult.of(getId(), validAccessIDs, "java.lang.Long");
    }
    else {
      return ListResult.error(getId(), "java.lang.Long");
    }

  }

}


