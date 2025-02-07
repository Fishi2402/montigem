<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "parameters", "types", "useDeser", "objectMapperName", "makeAbstract", "optionals")}
<#if makeAbstract>
${cd4c.method("protected abstract Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
<#else>
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
  return Optional.of(new ${command}(
<#list parameters as param>
    <#assign type = types[param?index]>
    ${tc.includeArgs("umlp.commands.deser.OwnCommand_FromJsonObjectParameters", [param, genService.printTypeFullName(type), genService.isOptional(type), genService.isCDOrInnerCDEnum(type), genService.isList(type), genService.calculateJsonInclGenerics(type, param), useDeser[param?index], objectMapperName, optionals[param?index]])}<#if param?index != parameters?size -1>,</#if>
</#list>
  ));
</#if>
