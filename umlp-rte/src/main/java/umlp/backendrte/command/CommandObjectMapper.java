/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

// RTE-P

import de.se_rwth.commons.logging.Log;

import java.util.Objects;
import java.util.Optional;

// Serialisiert und deserialisiert Commands und CommandResults
public abstract class CommandObjectMapper {
  protected static CommandObjectMapper instance;

  protected static CommandObjectMapper getInstance(){
    return Objects.requireNonNull(CommandObjectMapper.instance, "CommandObjectMapper is not initialized!");
  }

  public static String serializeCommand(Command<?> command){
    Exception err;
    try {
      Optional<String> opt = getInstance()._serializeCommand(command);
      if(opt.isPresent()) {
        return opt.get();
      }else{
        err = new IllegalStateException("Instance returned Optional.empty");
      }
    } catch (Exception e) {
      err = e;
    }

    String msg = String.format("%s Could not serialize command: %s\n%s", "0xS0004", command.getCommandType(), err.getMessage());
    Log.warn(msg, err);
    return getInstance()._serializeCommand(new ServerError(err)).get();
  }

  protected abstract Optional<String> _serializeCommand(Command<?> command);


  public static Optional<Command<Object>> deserializeCommand(String cmdStr){
    try {
      return getInstance()._deserializeCommand(cmdStr);
    } catch (Exception e) {
      String msg = String.format("%s Could not deserialize command: %s\n%s", "0xS0003", cmdStr, e.getMessage());
      Log.warn(msg, e);
      return Optional.empty();
    }
  }

  protected abstract Optional<Command<Object>> _deserializeCommand(String cmdStr);

  public static String serializeResponse(CommandResult<?> result){
    try {
      return getInstance()._serializeResponse(result);
    } catch (Exception e) {
      String msg = String.format("%s Could not serialize result: %s\n%s", "0xS0001", result.getTypeParameter(), e.getMessage());
      Log.warn(msg, e);
      return getInstance()._serializeResponse(StatusResult.error(result.getCommandId()));
    }
  }

  public abstract String _serializeResponse(CommandResult<?> result);

  public static Optional<CommandResult<?>> deserializeResponse(String responseStr){
    try {
      return getInstance()._deserializeResponse(responseStr);
    } catch (Exception e) {
      String msg = String.format("%s Could not deserialize result : %s\n%s", "0xS0002", responseStr, e.getMessage());
      Log.warn(msg, e);
      return Optional.empty();
    }
  }

  protected abstract Optional<CommandResult<?>> _deserializeResponse(String responseStr);
}
