<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "returnType", "paramSignature", "paramCallsite", "isVoid")}
${cd4c.method("public ${returnType} ${name}(${paramSignature})")}

var monitor = MethodMonitor.start("${name}");
try {
<#if isVoid>
  super.${name}(${paramCallsite});
  monitor.reportEnd(null); //TODO report void instead of null
<#else>
  var res = super.${name}(${paramCallsite});
  monitor.reportEnd(res);
  return res;
</#if>
} catch (Exception e) {
  monitor.reportError(e);
  throw e;
}
