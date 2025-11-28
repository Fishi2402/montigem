/* (c) https://github.com/MontiCore/monticore */
package inheritance;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import umlp.backendrte.command.CommandDirectCommunication;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.TestClientCommandManager;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4SQL;
import umlp.backendrte.common.GemList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
    @Override
    void setup() throws Exception {
      InheritanceTestContext.clear();
      InheritanceMock.init();
      backend = InheritanceTestContext.fromCurrentContext();
      setupFrontend();
    }

    @Override
    void cleanup() throws Exception {

    }
  }

  static class WithDBBackend extends Setup {
    @Override
    void setup() throws Exception {
      InheritanceTestContext.clear();
      DatabaseAccessManager4SQL.init();
      InheritanceManagerImpl.init();
      String schema = IOUtils.resourceToString("/Inheritance.sql", StandardCharsets.UTF_8);
      assertTrue(DatabaseAccessManager4SQL.initSchema(schema));
      backend = InheritanceTestContext.fromCurrentContext();
      setupFrontend();
    }
    @Override
    void cleanup() throws Exception {
      DatabaseAccessManager.closeConnection();
    }
  }

  static class SetupProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of(new WithMockBackend()),
          Arguments.of(new WithDBBackend())
      );
    }
  }

  @ParameterizedTest
  @ArgumentsSource(SetupProvider.class)
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
  @ArgumentsSource(SetupProvider.class)
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
