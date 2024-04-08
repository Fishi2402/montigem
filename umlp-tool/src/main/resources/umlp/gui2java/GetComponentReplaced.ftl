<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "returnType", "replaced", "args", "condition")}
${cd4c.method("public ${returnType} ${methodName}(${args})")}
<#if condition?has_content>
  if (${condition}) {
    <#if returnType=="boolean">
      return false; // cannot convert from null to boolean
    <#elseif returnType=="int" || returnType=="short" || returnType=="long" || returnType=="float" || returnType=="double" || returnType=="byte" || returnType=="char">
      return 0; // cannot convert from null to ${returnType}
    <#else>
      return null;
    </#if>
  }
</#if>
    return ${replaced};
