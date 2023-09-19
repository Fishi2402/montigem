/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.Issue;
import sehub.IssueAccessPolicy;
import umlp.backendrte.command.StatusResult;

public class Issue_create extends Issue_createTOP {

  public Issue_create(Issue issue) {
    super(issue);
  }

  public StatusResult doRun() {
    IssueAccessPolicy accessPolicy = new IssueAccessPolicy();
    if(accessPolicy.checkCUDPermission(this)){
      return super.doRun();
    }
    return StatusResult.error(getId());
  }


}


