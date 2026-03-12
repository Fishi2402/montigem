<#-- (c) https://github.com/MontiCore/monticore -->
${signature("statement","attr","columnName", "neo4j")}
<#assign type=genService.getDBType(attr.getSymbol().getType(), neo4j)>
<#assign typeConversion=genService.getTypeConversionCall(attr.getSymbol().getType(), neo4j)>
<#if type?contains(".")>
  <#assign tmplName=type?replace(".", "_")>
  <#if genService.isCDEnum(attr.getSymbol().getType())>
    ${includeArgs("umlp.dao.get."+tmplName, statement, columnName, attr.getSymbol().getType().print())}
  <#elseif genService.isInnerCDEnum(attr.getSymbol().getType())>
      ${includeArgs("umlp.dao.get."+tmplName, statement, columnName, genService.getInnerType(attr.getMCType()))}
  <#else>
    ${includeArgs("umlp.dao.get."+tmplName+neo4j, statement, columnName)}
  </#if>
<#else>
  ${statement}.getObject(${columnName}) == null? null : ((${type}) ${statement}.getObject(${columnName}))${typeConversion}
</#if>
