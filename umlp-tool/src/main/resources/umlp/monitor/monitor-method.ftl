<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "returnType", "params")}
${cd4c.method("public ${returnType} ${name}(${params
  .stream()
  .map(param -> param.getMCType().printType() + " " + param.getName())
  .collect(Collectors.joining(", "))})")}

var monitor = MethodMonitor.start("${name}");
try {
<#if returnType.equals("void")>
  super.${name}(${params.stream().map(param -> param.getName()).collect(Collectors.joining(", "))});
  monitor.reportEnd(null); //TODO report void instead of null
<#else>
  var res = super.${name}(${params.stream().map(param -> param.getName()).collect(Collectors.joining(", "))});
  monitor.reportEnd(res);
  return res;
</#if>
} catch (Exception e) {
  monitor.reportError(e);
  throw e;
}
