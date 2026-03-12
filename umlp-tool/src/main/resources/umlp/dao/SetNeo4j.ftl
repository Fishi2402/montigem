<#-- (c) https://github.com/MontiCore/monticore -->
${signature("statement","index","type","columnName", "isOptional","neo4j")}
<#if type?contains(".")>
  <#assign tmplName=type?replace(".", "_")>
  ${includeArgs("umlp.dao.set."+tmplName+neo4j, statement, columnName, index, isOptional)}
<#else>
  "${index}", ${columnName}<#if isOptional>.orElse(null)</#if>
</#if>
