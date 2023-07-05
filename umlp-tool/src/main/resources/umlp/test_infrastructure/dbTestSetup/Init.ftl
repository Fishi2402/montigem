<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void init() throws Exception")}

${cdName}TestContext.clear();
${cdName}ManagerImpl.init();
DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");
String schema = IOUtils.resourceToString("/${cdName}.sql", StandardCharsets.UTF_8);
assert DatabaseAccessManager.initSchema(schema);
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
