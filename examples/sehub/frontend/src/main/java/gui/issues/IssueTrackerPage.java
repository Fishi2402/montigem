package gui.issues;/* (c) https://github.com/MontiCore/monticore */

import sehub.Issue;
import sehub.IssueState;
import sehub.SehubSecurityManager;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component()
public class IssueTrackerPage extends IssueTrackerPageTOP {

  @Override
  public void init() {
    super.init();
    if (!SehubSecurityManager.isSignedIn()) {
      Router.navigate("gui/login/LoginPage");
    }
  }

  @Override
  public List<Issue> getTodoIssues(List<Issue> issues) {
    List<Issue> tmp = new ArrayList<>();
    for (Issue i : issues) {
      if (i.getState() == IssueState.UNASSIGNED) {
        tmp.add(i);
      }
    }
    return tmp;
  }

  @Override
  public List<Issue> getDoingIssues(List<Issue> issues) {
    List<Issue> tmp = new ArrayList<>();
    for (Issue i : this.getIssues()) {
      if (i.getState() == IssueState.IN_PROGRESS) {
        tmp.add(i);
      }
    }
    return tmp;
  }

  @Override
  public List<Issue> getDoneIssues(List<Issue> issues)  {
    List<Issue> tmp = new ArrayList();
    for (Issue i : this.getIssues()) {
      if (i.getState() == IssueState.DONE) {
        tmp.add(i);
      }
    }
    return tmp;
  }

  public void todoListDrop(Issue issue) {
    if (issue != null) {
      issue.setState(IssueState.UNASSIGNED);
    }
  }

  public void doingListDrop(Issue issue)  {
    if (issue != null) {
      issue.setState(IssueState.IN_PROGRESS);
    }
  }

  public void doneListDrop(Issue issue)  {
    if (issue != null) {
      issue.setState(IssueState.DONE);
    }
  }

}
