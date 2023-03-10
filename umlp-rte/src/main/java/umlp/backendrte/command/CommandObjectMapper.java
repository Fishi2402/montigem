/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

// RTE-P

import java.util.Objects;
import java.util.Optional;

// Serialisiert und deserialisiert Commands und CommandResults
public abstract class CommandObjectMapper {
  protected static CommandObjectMapper instance;

  protected static CommandObjectMapper getInstance(){
    return Objects.requireNonNull(instance, "CommandObjectMapper is not initialized!");
  }

  public static String serializeCommand(Command<?> command){
    return getInstance()._serializeCommand(command);
  }

  protected abstract String _serializeCommand(Command<?> command);


  public static Optional<Command<Object>> deserializeCommand(String cmdStr){
    return getInstance()._deserializeCommand(cmdStr);
  }

  protected abstract Optional<Command<Object>> _deserializeCommand(String cmdStr);

  public static String serializeResponse(CommandResult<?> result){
    return getInstance()._serializeResponse(result);
  }

  public abstract String _serializeResponse(CommandResult<?> result);

  public static Optional<CommandResult<?>> deserializeResponse(String responseStr){
    return getInstance()._deserializeResponse(responseStr);
  }

  protected abstract Optional<CommandResult<?>> _deserializeResponse(String responseStr);
}
