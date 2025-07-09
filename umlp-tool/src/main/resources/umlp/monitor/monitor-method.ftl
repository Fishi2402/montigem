<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "returnType", "paramSignature", "paramCallsite", "isVoid", "paramsPresent", "actualCall")}
${cd4c.method("public ${returnType} ${name}(${paramSignature})")}

var monitor = MethodMonitor.start(this, "${name}" <#if paramsPresent>, ${paramCallsite}</#if>);
try {
<#if isVoid>
  ${actualCall};
  monitor.reportReturnVoid();
<#else>
  var res = ${actualCall};
  monitor.reportReturn(res);
  return res;
</#if>
} catch (Exception e) {
  monitor.reportError(e);
  throw e;
}
