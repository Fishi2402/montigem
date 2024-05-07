<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void _init() throws Exception")}
dbStarter.start(this::getDBPath);
${cdName}TestContext.clear();
DatabaseAccessManager4Neo4j.initBoltLocalhost(dbStarter.getPort(), "neo4j", "");

${cdName}ManagerImpl.init();
${cdName}DAOFactory.initNeo4J();
super._init();

var be = getBackend().getManager();
${cdName}DAOFactory.addAccessListener(() -> {
  ${cdName}Manager inst = ${cdName}Manager.getSingleton();
  if (inst != be) {
    Log.error("DAO instances may only be accessed when the backend is active. " +
    "Please activate the backend before accessing the DAOFactory. " +
    "Current manager: " + inst);
  }
});
