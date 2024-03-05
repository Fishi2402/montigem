<#-- (c) https://github.com/MontiCore/monticore -->
${signature("statement","index","type","columnName", "isOptional","neo4j")}
<#if type?contains(".")>
  <#assign tmplName=type?replace(".", "_")>
    <#if tmplName == "java_lang_Enum">
        ${includeArgs("umlp.dao.set."+tmplName, statement, columnName, index, isOptional)}
    <#else>
        ${includeArgs("umlp.dao.set."+tmplName+neo4j, statement, columnName, index, isOptional)}
    </#if>
<#else>
  ${statement}.setObject(${index}, ${columnName}<#if isOptional>.orElse(null)</#if>);
</#if>
