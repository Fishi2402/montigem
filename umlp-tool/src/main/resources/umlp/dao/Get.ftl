${signature("statement", "type","columnName")}
<#if type?contains(".")>
  <#assign tmplName=type?replace(".", "_") >
  ${includeArgs("umlp.dao.get."+tmplName, statement, columnName)}
<#else>
  ${statement}.get${type}("${columnName}")
</#if>