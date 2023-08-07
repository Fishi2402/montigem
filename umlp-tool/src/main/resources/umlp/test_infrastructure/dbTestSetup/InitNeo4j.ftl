<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void _init() throws Exception")}

DatabaseAccessManager4Neo4j.init();
${cdName}TestContext.clear();
${cdName}ManagerImpl.init();
DatabaseAccessManager.executeStatement("MATCH(n) DETACH DELETE n");
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
