<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("commandList", "initializationTypeName", "baseName")}
${cd4c.constructor("public ${initializationTypeName}()")}
  // Standard
  deSerMap.put("${baseName}.commands.GetFreeGemIdRequest", new ${baseName}.commands.GetFreeGemIdRequestDeSer());

  // Generated
<#list commandList as command>
  deSerMap.put("${command}", new ${command}DeSer());
</#list>
