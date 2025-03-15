package loadingtests.ocl;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import loadingtests.LoadingTestsManager;
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
    LoadingTestsManager.personBuilder().age(49).name("Alice").build();
    LoadingTestsManager.personBuilder().age(20).name("Nico").build();
    LoadingTestsManager.personBuilder().age(19).name("Bob").build();

    String commandStr = "{\"id\":12,\"commandType\":\"loadingtests.commands.Person_get_filtered\",\"participantId\":1,\"filter\":\""
        + "[ p | p in Person,"
        + " p.age > 18,"
        + " p.age < 23,"
        + " p.age >= 15,"
        + " p.age <= 66,"
        + " p.name == \\\"Alice\\\","
        + " p.name != \\\"Bob\\\"]"
        + "\" }";

    Optional<Command<Object>> commandOptional = CommandObjectMapper.deserializeCommand(commandStr);
    assertTrue(commandOptional.isPresent());
    Command<Object> command = commandOptional.get();
    CommandResult<Object> res = CommandManager.executeCommand(command);
    assertNotNull(res);
    assertTrue(Log.getFindings().isEmpty());
    assertTrue(res.isListResult());
    List<Object> objs = res.asListResult().get();
    assertEquals(2, res.asListResult().get().size());
    // result contains Alices with gemId 0 and 1
    assertTrue(objs.contains(0L));
    assertTrue(objs.contains(1L));
  }

}
