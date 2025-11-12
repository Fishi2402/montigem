package gui.issues;/* (c) https://github.com/MontiCore/monticore */

import sehub.IssueState;
import sehub.SehubManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component()
public class IssueCreate extends IssueCreateTOP {

  @Override
  public Consumer<Void> createIssue(){
    return $ -> {
      this.getIssue().setState(IssueState.UNASSIGNED);
      this.getIssue().build();
      this.setIssue(SehubManager.issueBuilder());
    };
  }

}
