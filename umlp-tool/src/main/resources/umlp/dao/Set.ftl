<#-- (c) https://github.com/MontiCore/monticore -->
${signature("statement","index","type","columnName", "isOptional")}
<#if type?contains(".")>
  <#assign tmplName=type?replace(".", "_") >
  ${includeArgs("umlp.dao.set."+tmplName, statement, columnName, index, isOptional)}
<#else>
  ${statement}.set${type}(${index}, ${columnName}<#if isOptional>.orElse(null)</#if>)
</#if>
