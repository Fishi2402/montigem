/* (c) https://github.com/MontiCore/monticore */
package sehub.commands.associations;

import sehub.PostAccessPolicy;
import umlp.backendrte.command.StatusResult;

public class Resp_remove extends Resp_removeTOP {

  public Resp_remove(long parentGemId, long responsesGemId, int parentIndex, int responsesIndex) {
    super(parentGemId, responsesGemId, parentIndex, responsesIndex);
  }

  public StatusResult doRun() {
    PostAccessPolicy accessPolicy = new PostAccessPolicy();
    if (accessPolicy.checkWritePermission(this)) {
      return super.doRun();
    }
    return StatusResult.error(getId());
  }

}


