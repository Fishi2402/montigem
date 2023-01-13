<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oneSideName", "otherSideName", "oneSideSQLName","otherSideSQLName", "sqlTable", "numerusOfId")}
<#assign methodName = otherSideName?cap_first+"Id"+numerusOfId+"For"+oneSideName?cap_first>
${cd4c.method("public void ensureSelect${methodName}Created()")}
  if(select${methodName} == null) {
    select${methodName} = DatabaseAccessManager.prepareStatement("SELECT ${otherSideSQLName}_id FROM ${sqlTable}<#t>
    <#lt> WHERE ${oneSideSQLName}_id = ?");
  }
