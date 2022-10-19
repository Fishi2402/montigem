package hardy;

// GEN-P

import de.monticore.symboltable.serialization.JsonParser;
import umlp.command.Command;
import umlp.command.CommandObjectMapper;
import umlp.command.CommandResult;

import java.util.Optional;

/**
 * Stellt dem CommandObjectMapper eine DeSer instanzen bereit, welche die Domainenklassen von Flensburg.umlp
 * (de)serialisieren können.
 */
public class FlensburgCommandObjectMapper extends CommandObjectMapper {
  protected FlensburgCommandDeSer commandDeSer = new FlensburgCommandDeSer();
  protected FlensburgCommandResultDeSer resultDeSer = new FlensburgCommandResultDeSer();

  public static void init(){
    instance = new FlensburgCommandObjectMapper();
  }

  @Override
  protected String _serializeCommand(Command<?> command) {
    return commandDeSer.toJson(command);
  }

  @Override
  protected Optional<Command<Object>> _deserializeCommand(String cmdStr) {
    return commandDeSer.fromJson(JsonParser.parseJsonObject(cmdStr));
  }

  @Override
  public String _serializeResponse(CommandResult<?> result) {
    return resultDeSer.toJson(result);
  }

  @Override
  protected Optional<CommandResult<?>> _deserializeResponse(String responseStr) {
    return resultDeSer.fromJson(responseStr);
  }
}
