<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocName", "leftSideSQLName", "rightSideSQLName", "sqlTable")}
${cd4c.method("public void ensureRemove${assocName?cap_first}Created()")}

    remove${assocName?cap_first} = "DELETE FROM ${sqlTable}<#t>
    <#lt> WHERE ${leftSideSQLName}_id = ? AND ${rightSideSQLName}_id =?";

