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
      ${type?keep_after_last(".")}.valueOf(jsonObject.getMember("${attrName}").${json}),
    <#else>
      +jsonObject["${attrName}"],
    </#if>
</#if>
    +jsonObject["indexInList"]
  );
</#if>
