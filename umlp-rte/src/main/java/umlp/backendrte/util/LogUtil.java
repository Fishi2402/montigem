/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.util;

// RTE-P
// RTE-T

import umlp.backendrte.command.CommandManager;

public class LogUtil {
  private LogUtil(){ }

  public static String getLoggerName(){
    long participantId = CommandManager.getParticipantId();
    return participantId == 0 ? "Server" : "Client " + participantId;
  }

  /**
   * Hides sensitive data in the payload, such as a password, so that
   * it can still be logged, but the respective sensitive passages
   * are exchanged with ???.
   *
   * @param payload The received input payload as json String
   * @return The same payload String with concealed password info
   */
  public static String concealSensitives(String payload) {
    return payload.replaceAll("\"password\":\".*?[^\\\\]\"", "\"password\":\"???\"");
  }
}
