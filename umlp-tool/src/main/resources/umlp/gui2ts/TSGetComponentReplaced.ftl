<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "returnType", "replaced", "args", "isLiteral", "condition")}
${cd4c.method("public ${returnType} ${methodName}(${args})")}
<#if condition?has_content>
  if (!this.${condition}) {
    return null;
  }
</#if>
    return <#if !args?has_content && !isLiteral>this.</#if>${replaced};