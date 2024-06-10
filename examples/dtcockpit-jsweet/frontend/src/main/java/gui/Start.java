/* (c) https://github.com/MontiCore/monticore */
package gui;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitSecurityManager;
import dtcockpit.Project;
import dtcockpit.User;
import security.GetCurrentUserRequest;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.common.GemList;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

@Component
public class Start extends StartTOP {
  public Start(GemList<Project> projects) {
    setProjects(projects);
  }

  @Override
  public void init() {
    if (!(DtcockpitSecurityManager.isCurrentlyLoggingIn() || DtcockpitSecurityManager.isSignedIn())) {
      Router.navigate("gui", "login", "LoginPage");
      return;
    }

    CommandResult<Long> commandResult = CommandManager.executeCommand(
        new GetCurrentUserRequest());
    if (commandResult.isSuccessful()) {
      Long value = commandResult.asSimpleResult().get();
      Log.info("Login", "GemId of current user is " + value);
      User userSure = DtcockpitManager.getUserSure(value);
      DtcockpitSecurityManager.signIn(userSure);
      Log.info("Login", "User login complete.");
    } else {
      Log.warn("Can not get GemId of current user");
    }
  }
}
