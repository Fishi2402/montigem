/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.Issue;
import sehub.IssueAccessPolicy;

public class Issue_create extends Issue_createTOP {

  public Issue_create(Issue issue) {
    super(issue);
  }

  @Override
  public boolean checkAccessPolicy() {
    IssueAccessPolicy accessPolicy = new IssueAccessPolicy();
    return accessPolicy.checkCUDPermission(this);
  }
}


