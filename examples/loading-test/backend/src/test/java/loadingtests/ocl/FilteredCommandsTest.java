package loadingtests.ocl;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import loadingtests.LoadingTestsManager;
import loadingtests.LoadingTestsQueryBuilderMap;
import loadingtests.test_infrastructure.LoadingTestsSQLDBTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.ocl.OCL2SQL;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilteredCommandsTest extends LoadingTestsSQLDBTestSetup {

  public FilteredCommandsTest() {
    super(0);
  }

  @BeforeEach
  public void localSetup() {
    OCL2SQL.init();
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();
    LoadingTestsQueryBuilderMap.init();
  }

  @Test
  public void testFilteredCommand() {
    getBackend().activate();

    // gemId == 0
    LoadingTestsManager.personBuilder().age(20).name("Alice").build();
    // gemId == 1
    LoadingTestsManager.personBuilder().age(19).name("Alice").build();
    // gemId == 2
    LoadingTestsManager.personBuilder().age(49).name("Alice").build();
    // gemId == 3
    LoadingTestsManager.personBuilder().age(20).name("Nico").build();
    // gemId == 4
    LoadingTestsManager.personBuilder().age(19).name("Bob").build();

    // Use 0 as the participantId
    String cmd1Str = "{\"id\":12,\"commandType\":\"loadingtests.commands.Person_get_filtered\",\"participantId\":0,\"filter\":\""
        + "[ p | p in Person,"
        + " p.age > 18,"
        + " p.age < 23,"
        + " p.age >= 15,"
        + " p.age <= 66,"
        + " p.name == \\\"Alice\\\","
        + " p.name != \\\"Bob\\\"]"
        + "\" }";

    Optional<Command<Object>> cmd1Optional = CommandObjectMapper.deserializeCommand(cmd1Str);
    assertTrue(cmd1Optional.isPresent());
    Command<Object> cmd1 = cmd1Optional.get();
    CommandResult<Object> res1 = CommandManager.executeCommand(cmd1);
    assertNotNull(res1);
    assertTrue(Log.getFindings().isEmpty());
    assertTrue(res1.isListResult());
    List<Object> objs1 = res1.asListResult().get();
    assertEquals(2, objs1.size());
    // result contains Alices with gemId 0 and 1
    assertTrue(objs1.contains(0L));
    assertTrue(objs1.contains(1L));

    String cmd2Str = "{\"id\":34,\"commandType\":\"loadingtests.commands.Person_get_filtered\",\"participantId\":0,\"filter\":\""
        + "[ p | p in Person,"
        + " p.name == \\\"Bob\\\"]"
        + "\" }";

    Optional<Command<Object>> cmd2Optional = CommandObjectMapper.deserializeCommand(cmd2Str);
    assertTrue(cmd2Optional.isPresent());
    Command<Object> cmd2 = cmd2Optional.get();
    CommandResult<Object> res2 = CommandManager.executeCommand(cmd2);
    assertNotNull(res2);
    assertTrue(Log.getFindings().isEmpty());
    assertTrue(res2.isListResult());
    List<Object> objs2 = res2.asListResult().get();
    assertEquals(1, objs2.size());
    // result contains Bob with gemId 4
    assertTrue(objs2.contains(4L));
  }

}
