/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar.db;

import de.se_rwth.commons.logging.Log;

import java.net.InetAddress;

public class LoadingFromDBHelper {

  public static InetAddress readIPFromStringWithoutException(String ip){
    try {
      return InetAddress.getByName(ip);
    } catch (Exception e) {
      Log.error("Could read IP address '"+ip+"' from database.");
    }
    // TODO will the exception ever be thrown? and what to return if it is thrown?
    return InetAddress.getLoopbackAddress();
  }
}
