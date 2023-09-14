<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "isOptional", "isCDEnum", "json", "useDeSer", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("protected abstract Optional<${command}> fromJsonObject(any jsonObject)")}
<#else>
${cd4c.method("protected Optional<${command}> fromJsonObject(any jsonObject)")}
  return new ${command}(
    +jsonObject["gemId"],
<#if useDeSer>
    ${objectMapperName}.deserialize(typeof jsonObject["${attrName}"] === 'string' ? '"' + jsonObject["${attrName}"] + '"' : String(jsonObject["${attrName}"]), "${type}")
<#else>
    <#if isCDEnum>
    ${type?keep_after_last(".")}[(jsonObject["${attrName}"] as keyof typeof ${type?keep_after_last(".")})]
    <#else>
    jsonObject["${attrName}"]
    </#if>
</#if>
  );
</#if>
