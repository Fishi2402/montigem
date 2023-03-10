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
}
