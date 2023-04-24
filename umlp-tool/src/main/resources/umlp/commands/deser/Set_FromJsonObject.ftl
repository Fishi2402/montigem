<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("command", "attrName", "type", "isOptional", "isCDEnum", "json", "useDeSer", "objectMapperName", "makeAbstract")}
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
    <#if isCDEnum>
    ${type?keep_after_last(".")}.valueOf(jsonObject.getMember("${attrName}").${json})
    <#else>
    jsonObject.getMember("${attrName}").${json}
    </#if>
</#if>
<#if isOptional>)</#if>
  ));
</#if>
