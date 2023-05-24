<#-- (c) https://github.com/MontiCore/monticore -->
${signature("statement","attr","columnName")}
<#assign type=genService.getDBType(attr.getSymbol().getType())>
<#if type?contains(".")>
  <#assign tmplName=type?replace(".", "_") >
  <#if genService.isCDEnum(attr.getSymbol().getType())>
    ${includeArgs("umlp.dao.get."+tmplName, statement, columnName, attr.getSymbol().getType().print())}
  <#elseif genService.isInnerCDEnum(attr.getSymbol().getType())>
      ${includeArgs("umlp.dao.get."+tmplName, statement, columnName, genService.getInnerType(attr.getMCType()))}
  <#else>
    ${includeArgs("umlp.dao.get."+tmplName, statement, columnName)}
  </#if>
<#else>
  ${statement}.get${type}(${columnName})
</#if>
