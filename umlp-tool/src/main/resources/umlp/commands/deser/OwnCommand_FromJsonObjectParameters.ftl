<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "type", "isOptional", "isCDEnum", "isList", "json", "useDeSer", "objectMapperName", "isGemOptional")}
<#if useDeSer>
    ${objectMapperName}.<${type}>deserialize(jsonObject.getMember("${attrName}").isJsonString() ? '"'
      + jsonObject.getMember("${attrName}").getAsJsonString().getValue() + '"' : jsonObject.getMember("${attrName}").toString(), "${type}").orElse(null)
<#else>
    <#if isCDEnum>
    ${type?keep_after_last(".")}.valueOf(jsonObject.getMember("${attrName}").${json})
    <#elseif isList>
    ${json}
    <#else>
      <#if isOptional || isGemOptional>
      jsonObject.getMember("${attrName}") != null ?
        Optional.of(jsonObject.getMember("${attrName}").${json})
          : Optional.empty()
      <#else>
      jsonObject.getMember("${attrName}").${json}
      </#if>
    </#if>
</#if>
