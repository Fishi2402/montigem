package hardy.commands;

import hardy.FlensburgManager;
import hardy.FlensburgMock;
import hardy.Human;
import org.junit.jupiter.api.Test;
import umlp.command.Command;
import umlp.command.CommandObjectMapper;
import umlp.command.CommandResult;
import umlp.command.GemResult;
import umlp.rte.Gem;
import umlp.rte.LoadingState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandObjectMapperTest {

  @Test
  public void testCommandRoundtrip(){
    int commandId = 1;
    int gemId = 1001;
    Command<Human> getHuman = new Human_get(commandId, gemId);
    String commandStr = CommandObjectMapper.serializeCommand(getHuman);

    Command<?> resultUnknown = CommandObjectMapper.deserializeCommand(commandStr);
    assertEquals(Human_get.class.getName(), resultUnknown.getCommandType());

    assertTrue(resultUnknown instanceof Human_get);
    Human_get known = (Human_get) resultUnknown;

    assertEquals(commandId, known.getId());
    assertEquals(gemId, known.gemId);
  }

  @Test
  public void testGemResultRoundtrip(){
    FlensburgMock.init();

    int commandId = 1;
    int gemId = 1001;
    FlensburgMock.secretlyAdaptnextGemIds(gemId);
    int age = 20;
    Human h1001 = FlensburgManager.humanBuilder().age(age).build().get();

    GemResult<Human> result = new GemResult<>(commandId, new Gem<>(h1001), "hardy.Human");

    String resultStr = CommandObjectMapper.serializeResponse(result);

    CommandResult<?> unknownResult = CommandObjectMapper.deserializeResponse(resultStr);

    assertEquals(commandId, unknownResult.getCommandId());
    assertTrue(unknownResult.isGemResult());
    assertTrue(unknownResult instanceof GemResult);

    GemResult<?> gemRes = unknownResult.asGemResult();
    Gem<?> gem = gemRes.getResult();

    assertEquals(LoadingState.LOADED, gem.getLoadingState());
    Object obj = gem.get();

    assertTrue(obj instanceof Human);
    Human h1001deserialized = (Human) obj;

    assertEquals(gemId, h1001deserialized.getGemId());
    assertEquals(age, h1001deserialized.getAge());
  }
}