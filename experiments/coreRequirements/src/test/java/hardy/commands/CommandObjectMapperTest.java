package hardy.commands;

import hardy.FlensburgManager;
import hardy.FlensburgMock;
import hardy.Human;
import org.junit.jupiter.api.Test;
import umlp.command.Command;
import umlp.command.CommandObjectMapper;
import umlp.command.CommandResult;
import umlp.command.SimpleResult;
import umlp.rte.LoadingState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandObjectMapperTest {

  @Test
  public void testCommandRoundtrip(){
    int gemId = 1001;
    Command<Human> getHuman = new Human_get(gemId);
    String commandStr = CommandObjectMapper.serializeCommand(getHuman);

    Command<?> resultUnknown = CommandObjectMapper.deserializeCommand(commandStr);
    assertEquals(Human_get.class.getName(), resultUnknown.getCommandType());

    assertTrue(resultUnknown instanceof Human_get);
    Human_get known = (Human_get) resultUnknown;

    assertEquals(getHuman.getId(), known.getId());
    assertEquals(gemId, known.gemId);
  }

  @Test
  public void testSimpleResultRoundtrip(){
    FlensburgMock.init();

    int commandId = 1;
    int gemId = 1001;
    FlensburgMock.secretlyAdaptnextGemIds(gemId);
    int age = 20;
    Human h1001 = FlensburgManager.humanBuilder().age(age).build().get();

    SimpleResult<Human> result = new SimpleResult<>(commandId, h1001, "hardy.Human");

    String resultStr = CommandObjectMapper.serializeResponse(result);

    CommandResult<?> unknownResult = CommandObjectMapper.deserializeResponse(resultStr);

    assertEquals(commandId, unknownResult.getCommandId());
    assertTrue(unknownResult.isSimpleResult());
    assertTrue(unknownResult instanceof SimpleResult);

    SimpleResult<?> resp = unknownResult.asSimpleResult();

    assertEquals(LoadingState.LOADED, resp.getLoadingState());
    Object obj = resp.get();

    assertTrue(obj instanceof Human);
    Human h1001deserialized = (Human) obj;

    assertEquals(gemId, h1001deserialized.getGemId());
    assertEquals(age, h1001deserialized.getAge());
  }
}