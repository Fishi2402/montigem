<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("oneSideName", "otherSideName", "oneSideSQLName","otherSideSQLName", "sqlTable", "numerusOfId")}
<#assign methodName = otherSideName?cap_first+"Id"+numerusOfId+"For"+oneSideName?cap_first>
${cd4c.method("public void ensureSelect${methodName}Created()")}
select${methodName} = "SELECT ${otherSideSQLName}_id FROM ${sqlTable}<#t>
<#lt> WHERE ${oneSideSQLName}_id = ?";

