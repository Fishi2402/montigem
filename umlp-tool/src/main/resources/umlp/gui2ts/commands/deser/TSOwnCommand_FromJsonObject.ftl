<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "parameters", "types", "useDeser", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("protected abstract Optional<${command}> fromJsonObject(any jsonObject)")}
<#else>
${cd4c.method("protected Optional<${command}> fromJsonObject(any jsonObject)")}
  return new ${command}(
<#list parameters as param>
    <#assign type = types[param?index]>
    <#if !type.isObscureType()>
    ${tc.includeArgs("umlp.gui2ts.commands.deser.TSOwnCommand_FromJsonObjectParameters", [param, genService.printTypeFullName(type), genService.isOptional(type), genService.isCDOrInnerCDEnum(type), genService.isList(type), genService.calculateJsonInclGenerics(type, param), useDeser[param?index], objectMapperName])}<#if param?index != parameters?size -1>,</#if>
    <#else>
    jsonObject["${param}"]<#if param?index != parameters?size -1>,</#if>
    </#if>

</#list>
  );
</#if>
