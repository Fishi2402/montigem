/* (c) https://github.com/MontiCore/monticore */
package flensburg.commands;

import hardy.flensburg.Address;
import hardy.flensburg.FlensburgCommandObjectMapper;
import hardy.flensburg.FlensburgManager;
import hardy.flensburg.Human;
import hardy.flensburg.Person;
import hardy.flensburg.commands.Human_get;
import hardy.flensburg.test_infrastructure.FlensburgMockTestSetup;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.Gem;
import umlp.backendrte.common.LoadingState;
import umlp.test_rte.ClientSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandObjectMapperTest extends FlensburgMockTestSetup {

  public CommandObjectMapperTest() {
    super(ClientSetup.of(1));
  }

  @Override
  public void _init() throws Exception {
    super._init();
    FlensburgCommandObjectMapper.init();
  }

  @Test
  public void testCommandRoundtrip(){
    int gemId = 1001;
    Command<Human> getHuman = new Human_get(gemId);
    String commandStr = CommandObjectMapper.serializeCommand(getHuman);

    Command<?> resultUnknown = CommandObjectMapper.deserializeCommand(commandStr).get();
    assertEquals(Human_get.class.getName(), resultUnknown.getCommandType());

    assertTrue(resultUnknown instanceof Human_get);
    Human_get known = (Human_get) resultUnknown;

    assertEquals(getHuman.getId(), known.getId());
    assertEquals(gemId, known.getGemId());
  }

  @Test
  public void testSimpleResultRoundtrip(){
    int commandId = 1;
    int gemId = 1001;
    int age = 20;
    Human h1001 = FlensburgManager.humanBuilderFromId(gemId).age(age).build().get();

    SimpleResult<Human> result = new SimpleResult<>(commandId, h1001, "hardy.flensburg.Human");

    String resultStr = CommandObjectMapper.serializeResponse(result);

    CommandResult<?> unknownResult = CommandObjectMapper.deserializeResponse(resultStr).get();

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

  @Test
  public void testSimpleStringResultRoundtrip(){
    int commandId = 1;

    String myString = "My Special String";

    SimpleResult<String> result = new SimpleResult<>(commandId, StringEscapeUtils.escapeJson(myString), "java.lang.String");

    String resultStr = CommandObjectMapper.serializeResponse(result);

    CommandResult<?> unknownResult = CommandObjectMapper.deserializeResponse(resultStr).get();

    assertEquals(commandId, unknownResult.getCommandId());
    assertTrue(unknownResult.isSimpleResult());
    assertTrue(unknownResult instanceof SimpleResult);

    SimpleResult<?> resp = unknownResult.asSimpleResult();

    assertEquals(LoadingState.LOADED, resp.getLoadingState());
    Object obj = resp.get();

    assertTrue(obj instanceof String);
    String deserializedString = (String) obj;
    System.out.println(myString);
    System.out.println(deserializedString);
    assertEquals(myString, deserializedString);
  }

  @Test
  public void testGemSerializedUnloaded(){

    int commandId = 1;
    int gemId = 1001;
    int age = 20;

    Address a1001 = FlensburgManager.addressBuilderFromId(gemId).city("Flensburg").build().get();
    Person p1002 = FlensburgManager
        .personBuilder()
        .age(age)
        .nickname("Smith")
        .mainResidence(a1001)
        .build()
        .get();

    SimpleResult<Person> resp = new SimpleResult<>(commandId, p1002, "hardy.flensburg.Person");

    String respStr = CommandObjectMapper.serializeResponse(resp);
    CommandResult<?> recreatedResp = CommandObjectMapper.deserializeResponse(respStr).get();

    assertTrue(recreatedResp.isSimpleResult());

    SimpleResult<?> simpleRes = recreatedResp.asSimpleResult();
    assertTrue(simpleRes.isSuccessful());

    assertTrue(simpleRes.get() instanceof Person);
    Person recreatedPerson = (Person) simpleRes.get();

    assertNotSame(p1002, recreatedPerson);
    assertEquals(p1002.getGemId(), recreatedPerson.getGemId());

    Gem<Address> mainResidenceGem = recreatedPerson.getMainResidenceGem();
    assertEquals(LoadingState.NOT_YET_LOADED, mainResidenceGem.getLoadingState());
  }
}
