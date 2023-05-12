<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "isCDEnum", "json", "useDeSer", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("protected abstract Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
<#else>
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
  return new ${command}(
    jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong(),
<#if useDeSer>
    ${objectMapperName}.<${type}>deserialize(jsonObject.isJsonString() ? '"' + jsonObject.getAsJsonString().getValue() + '"' : String(jsonObject), "${type}"),
<#else>
    <#if isCDEnum>
      ${type?keep_after_last(".")}.valueOf(jsonObject.getMember("${attrName}").${json}),
    <#else>
      jsonObject.getMember("${attrName}").${json},
    </#if>
</#if>
    jsonObject.getMember("indexInList").getAsJsonNumber().getNumberAsInteger()
  );
</#if>
