/* (c) https://github.com/MontiCore/monticore */
package gui.login;

import dtcockpit.DtcockpitSecurityManager;
import dtcockpit.User;
import umlp.backendrte.common.GemList;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

@Component()
public class UserLogin extends UserLoginTOP {
  public UserLogin(GemList users, User loginUser) {
    super(users, loginUser);
  }

  @Override
  public void init() {
    if (DtcockpitSecurityManager.isSignedIn()) {
      Router.navigate("gui", "Start");
    }

    if (this.loginUser == null) {
      this.loginUser = new User();
    }
  }

  protected String error = "";

  @Override
  public void loginOnEnter(Object event) {
    // TODO give event 'key'
//    if (event.key === "Enter") {
//      this.login(null);
//    }
  }

  @Override
  public void login(Object event) {
    if (this.loginUser == null || this.loginUser.getUsername() == null || this.loginUser.getPassword() == null) {
      return;
    }

    //TODO Ticket Das muss besser gehen!
//    const form = new FormData();
//
//    form.append('username', this.loginUser.getUsername());
//    form.append('password', this.loginUser.getPassword());
//
//    const requestOptions: RequestInit = {
//        method: "POST",
//        body: form,
//        redirect: 'manual', // Otherwise the redirect to login?error is followed and 2xx is returned
//        headers: {
//      'Accept': 'application/json'
//    }
//    };
//
//    fetch("/umlp/api/login", requestOptions)
//        .then(response => response.json())
//      .then(result => {
//    if (result.success) {
//      DtcockpitSecurityManager.setCurrentlyLoggingIng(true);
//      // Do NOT use router as otherwise the websocket is not refreshed!
//      // The AppComponent will initialize the websocket during reloading
//      window.location.href = '/gui/Start';
//    } else if (result.reason) {
//      alert("Login Failed: " + result.reason)
//    } else {
//      alert("Login Failed: Unknown")
//    }
//      })
//      .catch(error => {
//        console.warn(error);
//    alert("Login Failed: Unable to communicate");
//      });

    super.login(event);
  }
}
