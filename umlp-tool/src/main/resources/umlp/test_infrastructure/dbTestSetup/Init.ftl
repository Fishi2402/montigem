<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void _init() throws Exception")}

${cdName}TestContext.clear();
DatabaseAccessManager4SQL.init();
${cdName}ManagerImpl.init();
DatabaseAccessManager.executeStatement("DROP ALL OBJECTS");
String schema = IOUtils.resourceToString("/${cdName}.sql", StandardCharsets.UTF_8);
assert DatabaseAccessManager4SQL.initSchema(schema);
super._init();
