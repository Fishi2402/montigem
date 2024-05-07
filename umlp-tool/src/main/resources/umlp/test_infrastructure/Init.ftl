<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void _init() throws Exception")}

backend = ${cdName}TestContext.fromCurrentContext();

for(int i = 0; i < getNumberOfClients(); i++){
  ${cdName}TestContext.clear();
  ${cdName}Manager.initMe(new ${cdName}ManagerClientImpl());
  String userName = getClientName(i).orElse("");
  CommandDirectCommunication communication = new CommandDirectCommunication(backend, userName);
  TestClientCommandManager clientCommandManager = new TestClientCommandManager(communication);
  CommandManager.initMe(clientCommandManager);
  var client = ${cdName}TestContext.fromCurrentContext();
  communication.setClientContext(client);
  clients.add(client);
}

backend.activate();

//ensure correct context
${cdName}Manager be = getBackend().getManager();
be._addObserver(new EnsureCorrectManager${cdName}(be));

for (${cdName}TestContext client : getClientsList()) {
  client.getManager()._addObserver(new EnsureCorrectManager${cdName}(client.getManager()));
}

