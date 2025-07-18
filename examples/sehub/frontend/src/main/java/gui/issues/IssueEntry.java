package gui.issues;/* (c) https://github.com/MontiCore/monticore */

import sehub.IssueState;
import sehub.Post;
import sehub.SehubManager;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component()
public class IssueEntry extends IssueEntryTOP {

  public Consumer<Void> editIssue() {
    return $ -> {
      Router.navigate("/gui/NotesPage/" + this.getIssue().getGemId());
    };
  }

  public  Consumer<Void> promote() {
    return $ -> {
      if (this.getIssue().getState() == IssueState.UNASSIGNED) {
        this.getIssue().setState(IssueState.IN_PROGRESS);
      } else if (this.getIssue().getState() == IssueState.IN_PROGRESS) {
        this.getIssue().setState(IssueState.DONE);
      }
    };
  }

  public Consumer<Void> demote() {
    return $ -> {
      if (this.getIssue().getState() == IssueState.IN_PROGRESS) {
        this.getIssue().setState(IssueState.UNASSIGNED);
      } else if (this.getIssue().getState() == IssueState.DONE) {
        this.getIssue().setState(IssueState.IN_PROGRESS);
      }
    };
  }

  public  Consumer<Void> deleteIssue() {
    return $ -> SehubManager.deleteIssue(this.getIssue().getGemId());
  }

  public String getIssueCardTitle () {
    int postCounter = 0;

    for (Post p : this.getIssue().getPostsList()) {
      postCounter = postCounter + 1 + p.getResponsesList().size();
    }

    return super.getIssueCardTitle() + " (" + postCounter + ")";
  }

}
