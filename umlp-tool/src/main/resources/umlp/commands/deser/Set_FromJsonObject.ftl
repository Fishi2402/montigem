<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "isOptional", "json", "useDeSer", "objectMapperName", "makeAbstract")}
<#if makeAbstract>
${cd4c.method("protected abstract Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
<#else>
${cd4c.method("protected Optional<${command}> fromJsonObject(JsonObject jsonObject)")}
  return Optional.of(new ${command}(
    jsonObject.getMember("gemId").getAsJsonNumber().getNumberAsLong(),
<#if isOptional>Optional.ofNullable(</#if>
<#if useDeSer>
    ${objectMapperName}.<${type}>deserialize(jsonObject.getMember("${attrName}").isJsonString() ? '"' + jsonObject.getMember("${attrName}").getAsJsonString().getValue() + '"' : jsonObject.getMember("${attrName}").toString(), "${type}").orElse(null)
<#else>
    jsonObject.getMember("${attrName}").${json}
</#if>
<#if isOptional>)</#if>
  ));
</#if>
