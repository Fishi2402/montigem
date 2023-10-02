/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.Issue;
import sehub.IssueAccessPolicy;
import sehub.SehubManager;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.GeneralErrorCollector;
import umlp.backendrte.common.NotificationScope;

import java.util.Optional;

public class Issue_setDescription extends Issue_setDescriptionTOP {

  public Issue_setDescription(long gemId, String description) {
    super(gemId, description);
  }

  public StatusResult doAction() {
    Optional<Issue> issueOpt = SehubManager.issueOpt(getGemId());
    if (issueOpt.isPresent()) {
      issueOpt.get().setDescription(getDescription(), NotificationScope.LOCAL);
      return StatusResult.ok(getId());
    } else {
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(getId());
    }
  }

  @Override
  public boolean checkAccessPolicy() {
    IssueAccessPolicy accessPolicy = new IssueAccessPolicy();
    return accessPolicy.checkCUDPermission(this);
  }
}


