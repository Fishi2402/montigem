/* (c) https://github.com/MontiCore/monticore */
package dtcockpit;

import java.util.Objects;

public class DtcockpitSecurityManager {
  public static User getCurrentUser() {
    User currentUser = new User();
//    Object.assign(currentUser, JSON.parse(Objects.requireNonNull(localStorage.getItem("currentUser"))));
    return currentUser;
  }

  public static void setCurrentUser(User value) {
//    localStorage.setItem("currentUser", JSON.stringify(value));
  }

  public static Integer getCurrentUserIdOrNull() {
    return 0;
//    return DtcockpitSecurityManager.isSignedIn() ? DtcockpitSecurityManager.getCurrentUser().getGemId() : null;
  }

  public static boolean isCurrentlyLoggingIn() {
//    String item = localStorage.getItem("loggingIn");
//    return item != null && item.equals("true");
    return true;
  }

  public static void setCurrentlyLoggingIng(boolean val) {
//    localStorage.setItem("loggingIn", val ? "true" : "false");
  }

  public static void signIn(User value) {
//    this.setCurrentUser(value);
//    this.setCurrentlyLoggingIng(false);
  }

  public static void signOut() {
//    localStorage.removeItem("currentUser");
  }

  public static boolean isSignedIn() {
//    return localStorage.getItem("currentUser") != null;
    return true;
  }
}


