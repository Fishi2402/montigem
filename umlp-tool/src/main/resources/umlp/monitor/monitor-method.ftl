<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "returnType", "paramSignature", "paramCallsite", "isVoid", "paramsPresent", "actualCall")}
${cd4c.method("public ${returnType} ${name}(${paramSignature})")}

var monitor = MethodMonitor.start(this, "${name}" <#if paramsPresent>, ${paramCallsite}</#if>);
<#if !isVoid>${returnType} res;</#if>
try {
<#if isVoid>
  ${actualCall};
<#else>
  res = ${actualCall};
</#if>
} catch (Exception e) {
  monitor.reportError(e);
  throw e;
}
<#if isVoid>
  monitor.reportReturnVoid();
<#else>
  monitor.reportReturn(res);
  return res;
</#if>

