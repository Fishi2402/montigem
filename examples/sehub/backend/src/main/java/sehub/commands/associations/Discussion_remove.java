/* (c) https://github.com/MontiCore/monticore */
package sehub.commands.associations;

import sehub.PostAccessPolicy;
import umlp.backendrte.command.StatusResult;

public class Discussion_remove extends Discussion_removeTOP {

  public Discussion_remove(long issueGemId, long postsGemId, int issueIndex, int postsIndex) {
    super(issueGemId, postsGemId, issueIndex, postsIndex);
  }

  public StatusResult doRun() {
    PostAccessPolicy accessPolicy = new PostAccessPolicy();
    if (accessPolicy.checkWritePermission(this)) {
      return super.doRun();
    }
    return StatusResult.error(getId());
  }

}


