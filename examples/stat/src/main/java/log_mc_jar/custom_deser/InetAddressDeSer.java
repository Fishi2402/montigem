/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar.custom_deser;

import de.se_rwth.commons.logging.Log;
import log_mc_jar.db.LoadingFromDBHelper;
import umlp.backendrte.command.DeSer;

import java.net.InetAddress;
import java.util.Optional;

public class InetAddressDeSer implements DeSer<java.net.InetAddress> {
  @Override
  public String serialize(InetAddress obj) {
    return '"' + obj.toString() + '"';
  }

  @Override
  public Optional<InetAddress> deserialize(String str) {
    if(!str.matches("\".*\"")){
      Log.warn("InetAddress must be deserialized from json string");
      return Optional.empty();
    }

    return Optional.ofNullable(
        LoadingFromDBHelper.readIPFromStringWithoutException(str.substring(1, str.length() - 1))
    );
  }
}
