<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("commandList", "className", "baseName")}
${cd4c.constructor("public constructor()")}
public constructor() {
  super();
  // Standard
  this.deSerMap.set("${baseName}.requests.GetFreeGemIdRequest", new GetFreeGemIdRequestDeSer());

  // Generated
<#list commandList as command>
  this.deSerMap.set("${command}", new ${command?keep_after_last(".")}DeSer());
</#list>
}
