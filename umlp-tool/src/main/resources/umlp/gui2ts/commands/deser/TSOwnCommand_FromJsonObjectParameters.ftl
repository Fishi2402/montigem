<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "type", "isOptional", "isCDEnum", "isList", "json", "useDeSer", "objectMapperName")}
<#if isOptional>Optional.ofNullable(</#if>
<#if useDeSer>
    ${objectMapperName}.deserialize(typeof jsonObject["${attrName}"] === 'string' ? '"'
      + jsonObject["${attrName}"] + '"' : string(jsonObject["${attrName}"]), "${type}")
<#else>
    <#if isCDEnum>
    ${type?keep_after_last(".")}.valueOf(jsonObject.getMember("${attrName}").${json})
    <#elseif isList>
    this.${json}
    <#else>
    jsonObject["${attrName}"] //.${json}
    </#if>
</#if>
<#if isOptional>)</#if>
