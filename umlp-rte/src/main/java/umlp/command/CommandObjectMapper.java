package umlp.command;

// RTE-P

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.Objects;
import java.util.Optional;

// Serialisiert und deserialisiert Commands und CommandResults, mit Hilfe von Gson
// durch die Strategien CommandJsonDeserializer und CommandResultTypeAdapter
public abstract class CommandObjectMapper {
  protected static CommandObjectMapper instance;

  // Gson Instanz, welche Commands und CommandResults (de)serialisieren kann.
  // Um Domainenklassen wie Person zu (de)serialisieren, delegiert sie zu einer weiteren Gson Instanz(getDomainGson),
  // um eine Endlosschleife zu verhindern.
  protected static Gson gson = new GsonBuilder()
      .registerTypeHierarchyAdapter(Command.class, new CommandJsonDeserializer())
      .registerTypeHierarchyAdapter(CommandResult.class, new CommandResultTypeAdapter())
      .create();

  protected static CommandObjectMapper getInstance(){
    return Objects.requireNonNull(instance, "CommandObjectMapper is not initialized!");
  }

  // Gson Instanz welche Domainklassen wie Person (de)serialisieren kann, aber keine Commands oder CommandResults.
  // Muss pro Model generiert werden und wird per static delegate bereitgestellt.
  public static Gson getDomainGson(){
    return getInstance()._getDomainGson();
  }

  protected abstract Gson _getDomainGson();

  public static String serializeCommand(Command<?> command){
    return getInstance()._serializeCommand(command);
  }

  protected String _serializeCommand(Command<?> command) {
    return gson.toJson(command);
  }


  public static Optional<Command<Object>> deserializeCommand(String cmdStr){
    return getInstance()._deserializeCommand(cmdStr);
  }

  protected Optional<Command<Object>> _deserializeCommand(String cmdStr) {
    try {
      return Optional.of(gson.fromJson(cmdStr, Command.class));
    }
    catch (JsonSyntaxException e) {
      return Optional.empty();
    }
  }

  public static String serializeResponse(CommandResult<?> result){
    return gson.toJson(result);
  }

  public static Optional<CommandResult<?>> deserializeResponse(String responseStr){
    try {
      return Optional.of(gson.fromJson(responseStr, CommandResult.class));
    }
    catch (JsonSyntaxException e) {
      return Optional.empty();
    }

  }
}
