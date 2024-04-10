<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "returnType", "replaced", "args", "condition")}
${cd4c.method("public ${returnType} ${methodName}(${args})")}
<#if condition?has_content>
  if (${condition}) {
    return false;
  }
</#if>
    return ${replaced};
