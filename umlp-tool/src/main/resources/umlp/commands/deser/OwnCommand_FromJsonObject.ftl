<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "parameters", "types", "useDeser", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("protected abstract Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
<#else>
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
  return Optional.of(new ${command}(
    jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong()
<#list parameters as param>
    <#assign type = types[param?index]>
    ,${tc.includeArgs("umlp.commands.deser.OwnCommand_FromJsonObjectParameters", [param, genService.printTypeFullName(type), genService.isOptional(type), genService.isCDOrInnerCDEnum(type), genService.calculateJSonInclOptional(type), useDeser[param?index], objectMapperName])}
</#list>
  ));
</#if>
