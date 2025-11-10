package gui;/* (c) https://github.com/MontiCore/monticore */


import sehub.SehubManager;
import sehub.SehubSecurityManager;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;


@Component()
public class NotesPage extends NotesPageTOP {

  public void init() {
    if (!SehubSecurityManager.isSignedIn()) {
      def.dom.Globals.alert("No User Logged In");
      Router.navigate("gui/login/LoginPage");
    }
    super.init();
  }

  public Consumer<Void> newPost() {
    return $ -> {
      if (!SehubSecurityManager.isSignedIn()) {
        def.dom.Globals.alert("No User Logged In");
      } else {
        this.getIssue().addPosts(
            SehubManager.postBuilder()
                .author(SehubSecurityManager.getCurrentUser())
                .title("<< YOUR TITLE >>")
                .text("<< YOUR DESCRIPTION >> ")
                .issue(this.getIssue())
                .build().get()
        );
      }
    };
  }

}
