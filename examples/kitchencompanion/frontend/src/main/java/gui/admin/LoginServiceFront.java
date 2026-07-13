package gui.admin;

import kitchencompanion.User;

import java.util.Objects;

public abstract class LoginServiceFront {

  private static boolean loggedIn = false;

  public static void setLoggedIn(boolean loggedIn) {
    LoginServiceFront.loggedIn = loggedIn;
  }

  public static boolean getLoggedIn() {
    return loggedIn;
  }
  public static boolean canLogin(String password) {
    return password.equals(User.getPw());
  }
  public static void login(String password){
    if(canLogin(password)){
      setLoggedIn(true);
    }
  }
}
