<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("protected Path getDBPath(int port)")}
return Paths.get("build", "neo4j", "" + port);
