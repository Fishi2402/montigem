<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "isList")}

<#if isList>
  <#assign returnType = "GemList">
  <#assign paramType = "List<Long>">
<#else>
  <#assign returnType = "Gem">
  <#assign paramType = "long">
</#if>

${cd4c.method("protected  ${returnType}<${type}> _doCreate${name}(long id, ${paramType} o)")}

return null;
