/* (c) https://github.com/MontiCore/monticore */
package commands;

import com.google.common.collect.Lists;
import commands.commands.SendEmail;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.ListResult;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.command.StatusResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OwnCommandsTest {

  @Test
  public void testOwnCommandDeser() {
    CommandsMock.init();
    var user = CommandsManager.userBuilder().name("Testname").email("a@b.com").build().get();

    var command = new SendEmail(user.getGemId(), "my message", Optional.of("my subject"),
        Lists.newArrayList("f1", "f2", "f3"));
    var cmdStr = CommandObjectMapper.serializeCommand(command);

    Command<?> clonedCommand = CommandObjectMapper.deserializeCommand(cmdStr).get();
    assertTrue(clonedCommand instanceof SendEmail);
    assertEquals("my message", ((SendEmail) clonedCommand).getText());
    assertEquals("my subject", ((SendEmail) clonedCommand).getSubject());
    assertEquals(3, ((SendEmail) clonedCommand).getForwardsList().size());
    assertEquals("f1", ((SendEmail) clonedCommand).getForwards(0));
    assertEquals("f3", ((SendEmail) clonedCommand).getForwards(2));
    assertEquals(user.getGemId(), ((SendEmail) clonedCommand).getUserGemId());
  }

  @Test
  public void testOwnCommandResult() {
    CommandsMock.init();
    var user = CommandsManager.userBuilder().name("Testname").email("a@b.com").build().get();

    var command = new SendEmail(user.getGemId(), "my message", Optional.of("my subject"), new ArrayList<>());
    var cmdStr = CommandObjectMapper.serializeCommand(command);

    Command<?> clonedCommand = CommandObjectMapper.deserializeCommand(cmdStr).get();
    var result = CommandManager.executeCommand(clonedCommand);

    var resultStr = CommandObjectMapper.serializeResponse(result);
    var clonedResult = CommandObjectMapper.deserializeResponse(resultStr);

    assertNotNull(clonedResult);
    assertTrue(clonedResult.get().asStatusResult().isSuccessful());
    assertEquals(user.getGemId(), clonedResult.get().asStatusResult().getCommandId());
  }

  @Test
  public void testAppExecuteCommand() {
    CommandsMock.init();
    var user = CommandsManager.userBuilder().name("Testname").email("a@b.com").build().get();
    StatusResult result1 = App.executeSendEmail(user.getGemId(), "my message", Optional.of("my subject"), new ArrayList<>());
    assertTrue(result1.isSuccessful());

    SimpleResult<User> result2 = App.executeGetCDType(user.getGemId());
    assertTrue(result2.isSuccessful());
    assertEquals(result2.get().getGemId(), user.getGemId());
    assertEquals(result2.get().getName(), user.getName());
    assertEquals(result2.get().getEmail(), user.getEmail());

    ListResult<User, List<User>> result3 = App.executeGetCDTypeList();
    assertTrue(result3.isSuccessful());
    assertEquals(1, result3.get().size());
    assertEquals("Testname", result3.get().get(0).getName());
    assertEquals("a@b.com", result3.get().get(0).getEmail());
  }

}
