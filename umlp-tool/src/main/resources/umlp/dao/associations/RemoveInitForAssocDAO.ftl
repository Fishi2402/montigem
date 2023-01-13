<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "leftSideSQLName", "rightSideSQLName", "sqlTable")}
${cd4c.method("public void ensureRemove${assocName?cap_first}Created()")}
  if(remove${assocName?cap_first} == null) {
    remove${assocName?cap_first} = DatabaseAccessManager.prepareStatement("DELETE FROM ${sqlTable}<#t>
    <#lt> WHERE ${leftSideSQLName}_id = ? AND ${rightSideSQLName}_id =?");
  }
