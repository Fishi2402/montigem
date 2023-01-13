<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "leftSideSQLName", "rightSideSQLName", "sqlTable")}
${cd4c.method("public void ensureAdd${assocName?cap_first}Created()")}
  if(add${assocName?cap_first} == null) {
    add${assocName?cap_first} = DatabaseAccessManager.prepareStatement("INSERT INTO ${sqlTable} (<#t>
    <#lt>${leftSideSQLName}_id, ${rightSideSQLName}_id) VALUES (?, ?)");
  }
