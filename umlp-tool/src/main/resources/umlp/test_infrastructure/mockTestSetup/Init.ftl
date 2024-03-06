<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void _init()")}

${cdName}TestContext.clear();
${cdName}Mock.init();
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

//check dao access
${cdName}DAOFactory.init();
${cdName}DAOFactory.addAccessListener(() -> {
  ${cdName}Manager inst = ${cdName}Manager.getSingleton();
  if (inst != be) {
    Log.error("DAO instances may only be accessed when the backend is active. " +
    "Please activate the backend before accessing the DAOFactory. " +
    "Current manager: " + inst);
  }
});
