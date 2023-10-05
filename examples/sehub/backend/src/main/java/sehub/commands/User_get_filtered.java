/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.SehubManager;
import umlp.backendrte.command.ListResult;

import java.util.ArrayList;
import java.util.List;

public class User_get_filtered extends User_get_filteredTOP {

  public User_get_filtered() {
    super();
  }

  @Override
  public ListResult<Long, List<Long>> doAction() {

    List<Long> validAccessIDs = new ArrayList<>();
    for (long id : SehubManager.getUserList().getIDs()) {
      User_get atomicCmd = new User_get(id);
      atomicCmd.setParticipantId(this.getParticipantId());
      if (atomicCmd.doRun().isSuccessful()) {
        validAccessIDs.add(id);
      }
    }

    if (!validAccessIDs.isEmpty()) {
      return ListResult.of(getId(), validAccessIDs, "java.lang.Long");
    } else {
      return ListResult.error(getId(), "java.lang.Long");
    }

  }

}


