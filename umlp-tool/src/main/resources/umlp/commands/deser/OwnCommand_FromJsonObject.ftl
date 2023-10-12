<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "parameters", "types", "useDeser", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("protected abstract Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
<#else>
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
  return Optional.of(new ${command}(
<#list parameters as param>
    <#assign type = types[param?index]>
    ${tc.includeArgs("umlp.commands.deser.OwnCommand_FromJsonObjectParameters", [param, genService.printTypeFullName(type), genService.isOptional(type), genService.isCDOrInnerCDEnum(type), genService.calculateJsonInclGenerics(type), useDeser[param?index], objectMapperName])}<#if param?index != parameters?size -1>,</#if>
</#list>
  ));
</#if>
