<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocSideName", "startSideSQLName", "endSideSQLName", "sqlTable")}
${cd4c.method("public void ensureUpdate${assocSideName?cap_first}Created()")}
  if(update${assocSideName?cap_first} == null) {
    update${assocSideName?cap_first} = DatabaseAccessManager.prepareStatement("UPDATE ${sqlTable} SET <#t>
    <#lt>${endSideSQLName}_id = ? WHERE ${startSideSQLName}_id = ? AND ${endSideSQLName}_id = ?");
  }
