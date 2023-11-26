<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "parameters", "types", "useDeser", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
    ${cd4c.method("public abstract void buildJson(${command} command, JsonPrinter jsonPrinter)")}
<#else>
    ${cd4c.method("public void buildJson(${command} command, JsonPrinter jsonPrinter)")}
  super.buildJson(command, jsonPrinter);
<#list parameters as param>
    <#assign type = types[param?index]>
    ${tc.includeArgs("umlp.commands.deser.OwnCommand_BuildJsonParameters", [param, type, genService.printTypeFullName(type), genService.isCDOrInnerCDEnum(type), genService.calculateJsonInclGenerics(type, param), useDeser[param?index], objectMapperName])}
</#list>
</#if>
