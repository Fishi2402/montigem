package derivedattributesandassocs;

import derivedattributesandassocs.commands.Foo_calculateA;
import derivedattributesandassocs.commands.Foo_calculateB;
import derivedattributesandassocs.commands.Foo_calculateC;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CalculateCommandsTest {

  @Test
  public void testPrimitiveAttr(){
    DerivedAttributesAndAssocsMock.init();
    var foo = DerivedAttributesAndAssocsManager.fooBuilder().build().get();

    var command = new Foo_calculateA(foo.getGemId());
    var cmdStr = CommandObjectMapper.serializeCommand(command);

    var clonedCommand = CommandObjectMapper.deserializeCommand(cmdStr).get();
    var result = CommandManager.executeCommand(clonedCommand);

    var resultStr = CommandObjectMapper.serializeResponse(result);
    var clonedResult = CommandObjectMapper.deserializeResponse(resultStr);

    assertNotNull(clonedResult);
    assertNotNull(clonedResult);
    assertEquals(42, clonedResult.get().asSimpleResult().get());
  }

  @Test
  public void testStringAttr(){
    DerivedAttributesAndAssocsMock.init();
    var foo = DerivedAttributesAndAssocsManager.fooBuilder().build().get();

    var command = new Foo_calculateB(foo.getGemId());
    var cmdStr = CommandObjectMapper.serializeCommand(command);

    var clonedCommand = CommandObjectMapper.deserializeCommand(cmdStr).get();
    var result = CommandManager.executeCommand(clonedCommand);

    var resultStr = CommandObjectMapper.serializeResponse(result);
    var clonedResult = CommandObjectMapper.deserializeResponse(resultStr);

    assertNotNull(clonedResult);
    assertNotNull(clonedResult);
    assertEquals("test", clonedResult.get().asSimpleResult().get());
  }

  @Test
  public void testStringListAttr(){
    DerivedAttributesAndAssocsMock.init();
    var foo = DerivedAttributesAndAssocsManager.fooBuilder().build().get();

    var command = new Foo_calculateC(foo.getGemId());
    var cmdStr = CommandObjectMapper.serializeCommand(command);

    var clonedCommand = CommandObjectMapper.deserializeCommand(cmdStr).get();
    var result = CommandManager.executeCommand(clonedCommand);

    var resultStr = CommandObjectMapper.serializeResponse(result);
    var clonedResult = CommandObjectMapper.deserializeResponse(resultStr);

    assertNotNull(clonedResult);
    assertNotNull(clonedResult);
    assertEquals(List.of("eins", "zwei"), clonedResult.get().asListResult().get());
  }


}
