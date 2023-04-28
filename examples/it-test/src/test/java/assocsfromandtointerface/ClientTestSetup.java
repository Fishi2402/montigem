package assocsfromandtointerface;

import umlp.backendrte.command.CommandDirectCommunication;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.TestClientCommandManager;

class ClientTestSetup extends TestSetup {
  AssocsFromAndToInterfaceTestContext server;
  AssocsFromAndToInterfaceTestContext client;

  @Override
  public void initialize() {
    // Server
    AssocsFromAndToInterfaceMock.init();
    server = AssocsFromAndToInterfaceTestContext.fromCurrentContext();

    // Client
    AssocsFromAndToInterfaceManager.initMe(new AssocsFromAndToInterfaceManagerClientImpl4Test());
    CommandDirectCommunication communication = new CommandDirectCommunication(server);
    TestClientCommandManager clientCommandManager = new TestClientCommandManager(communication);
    CommandManager.initMe(clientCommandManager);
    client = AssocsFromAndToInterfaceTestContext.fromCurrentContext();
    communication.setClientContext(client);

    client.activate();
  }

  @Override
  public void clearCacheOfRemote() {
    ((AssocsFromAndToInterfaceManagerClientImpl4Test) client.getManager()).clearCache();
  }
}
