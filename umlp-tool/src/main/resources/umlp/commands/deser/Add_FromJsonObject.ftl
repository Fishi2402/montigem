<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "json", "useDeSer", "objectMapperName", "makeAbstract")}
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
    jsonObject.getMember("${attrName}").${json},
</#if>
    jsonObject.getMember("indexInList").getAsJsonNumber().getNumberAsInteger()
  ));
</#if>
