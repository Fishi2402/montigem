<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void _init()")}

${cdName}TestContext.clear();
${cdName}Mock.init();
backend = ${cdName}TestContext.fromCurrentContext();

for(int i = 0; i < getNumberOfClients(); i++){
  ${cdName}TestContext.clear();
  ${cdName}Manager.initMe(new ${cdName}ManagerClientImpl());
  CommandDirectCommunication communication = new CommandDirectCommunication(backend);
  TestClientCommandManager clientCommandManager = new TestClientCommandManager(communication);
  CommandManager.initMe(clientCommandManager);
  var client = ${cdName}TestContext.fromCurrentContext();
  communication.setClientContext(client);
  clients.add(client);
}

backend.activate();
