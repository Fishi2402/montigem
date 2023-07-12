package inheritance;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import umlp.backendrte.command.CommandDirectCommunication;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.TestClientCommandManager;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4SQL;
import umlp.backendrte.common.GemList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetFilteredTest {
  abstract static class Setup{
    InheritanceTestContext frontend;
    InheritanceTestContext backend;
    abstract void setup() throws Exception;
    void cleanup() throws Exception {}

    protected void setupFrontend() {
      InheritanceTestContext.clear();
      InheritanceManager.initMe(new InheritanceManagerClientImpl());
      CommandDirectCommunication communication = new CommandDirectCommunication(backend);
      TestClientCommandManager clientCommandManager = new TestClientCommandManager(communication);
      CommandManager.initMe(clientCommandManager);
      frontend = InheritanceTestContext.fromCurrentContext();
      communication.setClientContext(frontend);

      frontend.activate();
    }
  }

  static class WithMockBackend extends Setup {
    void setup() throws Exception {
      InheritanceTestContext.clear();
      InheritanceMock.init();
      backend = InheritanceTestContext.fromCurrentContext();
      setupFrontend();
    }

    @Override
    void cleanup() throws Exception {
      DatabaseAccessManager.closeConnection();
    }
  }

  static class WithDBBackend extends Setup {
    void setup() throws Exception {
      InheritanceTestContext.clear();
      DatabaseAccessManager4SQL.init();
      InheritanceManagerImpl.init();
      String schema = IOUtils.resourceToString("/Inheritance.sql", StandardCharsets.UTF_8);
      assertTrue(DatabaseAccessManager4SQL.initSchema(schema));
      backend = InheritanceTestContext.fromCurrentContext();
      setupFrontend();
    }
  }


  public static List<Setup> setups(){
    return List.of(new WithMockBackend(), new WithDBBackend());
  }

  @ParameterizedTest
  @MethodSource("setups")
  public void testAllInstancesOfConcreteSubclassReturned(Setup setup) throws Exception {
    setup.setup();
    try {
      List<A> as = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        as.add(InheritanceManager.aBuilder().aField(1).build().get());
      }

      GemList<A> aList = InheritanceManager.getAList();
      assertEquals(10, aList.size());

      for (A a : aList) {
        assertTrue(as.contains(a));
      }

    }finally {
      setup.cleanup();
    }
  }


  @ParameterizedTest
  @MethodSource("setups")
  public void testAllInstancesWithSubclassReturned(Setup setup) throws Exception {
    setup.setup();
    try {
      List<A> as = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        as.add(InheritanceManager.aBuilder().aField(1).build().get());
      }

      for (int i = 0; i < 5; i++) {
        as.add(InheritanceManager.dBuilder().aField(3).cField(5).dField(7).build().get());
      }

      GemList<A> aList = InheritanceManager.getAList();
      assertEquals(15, aList.size());

      for (A a : aList) {
        assertTrue(as.contains(a));
      }
    }finally {
      setup.cleanup();
    }
  }


}
