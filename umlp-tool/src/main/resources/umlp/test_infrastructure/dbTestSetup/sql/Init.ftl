<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void _init() throws Exception")}

${cdName}TestContext.clear();
DatabaseAccessManager4SQL.init();
${cdName}ManagerImpl.init();
DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");
String schema = IOUtils.resourceToString("/${cdName}.sql", StandardCharsets.UTF_8);
assert DatabaseAccessManager4SQL.initSchema(schema);
${cdName}DAOFactory.init();
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

