<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("commandList", "className", "baseName")}
${cd4c.constructor("public constructor()")}
  // Standard
  this.deSerMap.set("${baseName}.requests.GetFreeGemIdRequest", new ${baseName}.requests.GetFreeGemIdRequestDeSer());

  // Generated
<#list commandList as command>
  this.deSerMap.set("${command}", new ${command}DeSer());
</#list>
