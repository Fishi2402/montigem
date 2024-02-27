<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "parameters", "types", "useDeser", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
    ${cd4c.method("public abstract void buildJson(${command} command, any jsonPrinter)")}
<#else>
    ${cd4c.method("public void buildJson(${command} command, any jsonPrinter)")}
  super.buildJson(command, jsonPrinter);
<#list parameters as param>
    <#assign type = types[param?index]>
    <#if !type.isObscureType()>
  ${tc.includeArgs("umlp.gui2ts.commands.deser.TSOwnCommand_BuildJsonParameters", [param, type, genService.printTypeFullName(type), genService.isCDOrInnerCDEnum(type), genService.calculateJsonInclGenerics(type, param), useDeser[param?index], objectMapperName])}
    <#else>
  jsonPrinter["${param}"] = command.${genService.getGetterName(param, type)}();
    </#if>
</#list>
</#if>
