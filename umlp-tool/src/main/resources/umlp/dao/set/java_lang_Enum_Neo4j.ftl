${signature("statement","columnName", "index", "isOptional")}
<#if isOptional>
  "${index}", ${columnName}.map(Enum::name).orElse(null)
<#else>
  "${index}", ${columnName}.name()
</#if>
