<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "isCDEnum", "json", "useDeSer", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("protected abstract Optional<${command}> fromJsonObject(any jsonObject)")}
<#else>
${cd4c.method("protected Optional<${command}> fromJsonObject(any jsonObject)")}
  return new ${command}(
    +jsonObject["gemId"],
<#if useDeSer>
    ${objectMapperName}.<${type}>deserialize(jsonObject.isJsonString() ? '"' + jsonObject.getAsJsonString().getValue() + '"' : String(jsonObject), "${type}"),
<#else>
    <#if isCDEnum>
      ${type?keep_after_last(".")}[(jsonObject.getMember("${attrName}").${json}) as keyof typeof ${type?keep_after_last(".")}],
    <#else>
      +jsonObject["${attrName}"],
    </#if>
</#if>
    +jsonObject["indexInList"]
  );
</#if>
