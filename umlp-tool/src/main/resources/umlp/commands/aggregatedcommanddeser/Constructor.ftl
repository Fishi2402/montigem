<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("commandList", "className", "baseName")}
${cd4c.constructor("public ${className}()")}
  // Standard
  deSerMap.put("${baseName}.requests.GetFreeGemIdRequest", new ${baseName}.requests.GetFreeGemIdRequestDeSer());

  // Generated
<#list commandList as command>
  deSerMap.put("${command}", new ${command}DeSer());
</#list>
