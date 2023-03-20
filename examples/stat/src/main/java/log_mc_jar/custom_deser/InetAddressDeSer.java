/* (c) https://github.com/MontiCore/monticore */
package log_mc_jar.custom_deser;

import log_mc_jar.db.LoadingFromDBHelper;
import umlp.backendrte.command.DeSer;

import java.net.InetAddress;
import java.util.Optional;

public class InetAddressDeSer implements DeSer<java.net.InetAddress> {
  @Override
  public String serialize(InetAddress obj) {
    return obj.toString();
  }

  @Override
  public Optional<InetAddress> deserialize(String str) {
    return Optional.ofNullable(LoadingFromDBHelper.readIPFromStringWithoutException(str));
  }
}
