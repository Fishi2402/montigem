<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attr", "fieldType", "jsonStr", "isList", "innerType", "isOptional", "useDeSer", "objectMapperName")}
<#assign fieldName=attr.getName()>
<#if !jsonStr?has_content && !useDeSer>
<#--    TODO Kommentar einfuegen, wie das zu fixen waere-->
    ${cd4c.method("abstract protected ${fieldType} deserialize${fieldName?cap_first} (JsonObject json)")}
<#else>
    ${cd4c.method("protected ${fieldType} deserialize${fieldName?cap_first} (JsonObject json)")}
    <#if isList>let result: any[] = [];
      if(!json.hasMember("${fieldName}")) {
      return result;
      }
      for(let e of json.getArrayMember("${fieldName}")) {
      <#if useDeSer>
      result.push(${objectMapperName}.<${innerType}>deserialize(e.isJsonString() ? '"' + e.getAsJsonString().getValue() + '"' : String(e), "${innerType}"));
      <#elseif genService.isInnerCDEnum(attr.getSymbol().getType())>
<#--        TODO MB: ist das hier so richtig, oder sollte lieber innerType richtig berechnet werden? -->
      <#-- TODO AGe: find out if that's ok -->
      result.push(${innerType?keep_after_last(".")}.valueOf(json.getMember("${fieldName}").${jsonStr}));
      <#else>
      result.push(e.${jsonStr});
      </#if>
      }
      return result;
    <#elseif isOptional>
      if(json.hasMember("${fieldName}")){
      <#if useDeSer>
      let value: JsonElement = json.getMember("${fieldName}");
      return ${objectMapperName}.<${innerType}>deserialize(value.isJsonString() ? '"' + value.getAsJsonString().getValue() + '"' : value.toString(), "${innerType}");
      <#else>
        <#if genService.isInnerCDEnum(attr.getSymbol().getType())>
          return ${innerType?keep_after_last(".")}.valueOf(json.getMember("${fieldName}").${jsonStr});
        <#else>
          return json.getMember("${fieldName}").${jsonStr};
        </#if>
      </#if>
      } else {
      return null;
      }
    <#else>
    <#-- TODO: default values -->
      <#if useDeSer>
      JsonElement value = json.getMember("${fieldName}");
      return ${objectMapperName}.<${fieldType}>deserialize(value.isJsonString() ? '"' + value.getAsJsonString().getValue() + '"' : value.toString(), "${fieldType}");
      <#else>
        <#if genService.isCDEnum(attr.getSymbol().getType())>
      return ${fieldType}.valueOf(json.getMember("${fieldName}").${jsonStr});
        <#else>
      return json.getMember("${fieldName}").${jsonStr};
          </#if>
      </#if>
    </#if>

</#if>
