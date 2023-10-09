<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "type", "isOptional", "isCDEnum", "json", "useDeSer", "objectMapperName")}
<#if isOptional>Optional.ofNullable(</#if>
<#if useDeSer>
    ${objectMapperName}.<${type}>deserialize(jsonObject.getMember("${attrName}").isJsonString() ? '"'
      + jsonObject.getMember("${attrName}").getAsJsonString().getValue() + '"' : jsonObject.getMember("${attrName}").toString(), "${type}").orElse(null)
<#else>
    <#if isCDEnum>
    ${type?keep_after_last(".")}.valueOf(jsonObject.getMember("${attrName}").${json})
    <#else>
    jsonObject.getMember("${attrName}").${json}
    </#if>
</#if>
<#if isOptional>)</#if>
