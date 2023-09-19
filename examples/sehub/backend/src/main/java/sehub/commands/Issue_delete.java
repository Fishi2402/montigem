/* (c) https://github.com/MontiCore/monticore */

package sehub.commands;

import sehub.IssueAccessPolicy;
import sehub.SehubManager;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class Issue_delete extends Issue_deleteTOP {

  public Issue_delete(long gemId) {
    super(gemId);
  }


  public StatusResult doRun() {
    IssueAccessPolicy accessPolicy = new IssueAccessPolicy();
    if (accessPolicy.checkDeletePermission(this)) {
      if (SehubManager.deleteIssue(getGemId(), NotificationScope.LOCAL)) {
        return StatusResult.ok(getId());
      } else {
        return StatusResult.error(getId());
      }
    } else {
      return StatusResult.error(getId());
    }
  }

}


