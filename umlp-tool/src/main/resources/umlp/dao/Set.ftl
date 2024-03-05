<#-- (c) https://github.com/MontiCore/monticore -->
${signature("statement","index","type","columnName", "isOptional","neo4j")}
<#if type?contains(".")>
  <#assign tmplName=type?replace(".", "_")>
    <#if genService.isCDEnum(attr.getSymbol().getType()) || genService.isInnerCDEnum(attr.getSymbol().getType())>
        ${includeArgs("umlp.dao.set."+tmplName, statement, columnName, index, isOptional)}
    <#else>
        ${includeArgs("umlp.dao.set."+tmplName+neo4j, statement, columnName, index, isOptional)}
    </#if>
<#else>
  ${statement}.setObject(${index}, ${columnName}<#if isOptional>.orElse(null)</#if>);
</#if>
