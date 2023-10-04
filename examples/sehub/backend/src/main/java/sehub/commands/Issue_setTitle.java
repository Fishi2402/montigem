/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.Issue;
import sehub.IssueAccessPolicy;
import sehub.SehubManager;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.GeneralErrorCollector;
import umlp.backendrte.common.NotificationScope;

import java.util.Optional;

public class Issue_setTitle extends Issue_setTitleTOP {

  public Issue_setTitle(long gemId, String title) {
    super(gemId, title);
  }

  @Override
  public StatusResult doAction() {
    Optional<Issue> issueOpt = SehubManager.issueOpt(getGemId());
    if (issueOpt.isPresent()) {
      issueOpt.get().setTitle(getTitle(), NotificationScope.LOCAL);
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


