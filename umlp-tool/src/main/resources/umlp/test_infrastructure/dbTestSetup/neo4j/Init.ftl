<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void _init() throws Exception")}

${cdName}TestContext.clear();
DatabaseAccessManager4Neo4j.initBoltLocalhost(7687, "neo4j", "");

${cdName}DAOFactory.initNeo4J();
${cdName}ManagerImpl.init();
DatabaseAccessManager.executeStatement("MATCH(n) DETACH DELETE n");
super._init();
