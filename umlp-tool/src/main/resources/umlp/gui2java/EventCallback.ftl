<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "args", "params")}
<#if params?has_content>
  <#assign paramStr = "Object " + params?join(", Object ")>
<#else>
  <#assign paramStr = "">
</#if>
${cd4c.method("public Consumer<Object> ${methodName}(${paramStr})")}
  return event -> {};
