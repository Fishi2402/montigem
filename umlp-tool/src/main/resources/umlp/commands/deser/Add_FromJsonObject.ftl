<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "isCDEnum", "json", "useDeSer", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("protected abstract Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
<#else>
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
  return Optional.of(new ${command}(
    jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong(),
<#if useDeSer>
    JsonElement value = jsonObject.getMember("${attrName}");
    ${objectMapperName}.<${type}>deserialize(value.isJsonString() ? '"' + value.getAsJsonString().getValue() + '"' : value.toString(), "${type}").orElse(null),
<#else>
    <#if isCDEnum>
      ${type?keep_after_last(".")}.valueOf(jsonObject.getMember("${attrName}").${json}),
    <#else>
      jsonObject.getMember("${attrName}").${json},
    </#if>
</#if>
    jsonObject.getMember("indexInList").getAsJsonNumber().getNumberAsInteger()
  ));
</#if>
