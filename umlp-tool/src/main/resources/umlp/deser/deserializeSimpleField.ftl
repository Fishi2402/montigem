<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attr", "fieldType", "jsonStr", "isList", "innerType", "isOptional", "useDeSer", "objectMapperName")}
<#assign fieldName=attr.getName()>
<#if !jsonStr?has_content && !useDeSer>
<#--    TODO Kommentar einfuegen, wie das zu fixen waere-->
    ${cd4c.method("abstract protected ${fieldType} deserialize${fieldName?cap_first} (JsonObject json)")}
<#else>
    ${cd4c.method("protected ${fieldType} deserialize${fieldName?cap_first} (JsonObject json)")}
    <#if isList>${fieldType} result = new ArrayList<>();
      if(!json.hasMember("${fieldName}")) {
      return result;
      }
      for(de.monticore.symboltable.serialization.json.JsonElement e : json.getArrayMember("${fieldName}")) {
      <#if useDeSer>
      result.add(${objectMapperName}.<${innerType}>deserialize(e.isJsonString() ? '"' + e.getAsJsonString().getValue() + '"' : e.toString(), "${innerType}").orElse(null));
      <#elseif genService.isInnerCDEnum(attr.getSymbol().getType())>
      result.add(${innerType?keep_after_last(".")}.valueOf(json.getMember("${fieldName}").${jsonStr}));
      <#else>
      result.add(e.${jsonStr});
      </#if>
      }
      return result;
    <#elseif isOptional>
      if(json.hasMember("${fieldName}")){
      <#if useDeSer>
      JsonElement value = json.getMember("${fieldName}");
      return java.util.Optional.of(${objectMapperName}.<${innerType}>deserialize(value.isJsonString() ? '"' + value.getAsJsonString().getValue() + '"' : value.toString(), "${innerType}")).orElse(null);
      <#else>
        <#if genService.isInnerCDEnum(attr.getSymbol().getType())>
          return java.util.Optional.of(${innerType?keep_after_last(".")}.valueOf(json.getMember("${fieldName}").${jsonStr}));
        <#else>
          return java.util.Optional.of(json.getMember("${fieldName}").${jsonStr});
        </#if>
      </#if>
      } else {
      return java.util.Optional.empty();
      }
    <#else>
    <#-- TODO: default values -->
      <#if useDeSer>
      JsonElement value = json.getMember("${fieldName}");
      return  ${objectMapperName}.<${fieldType}>deserialize(value.isJsonString() ? '"' + value.getAsJsonString().getValue() + '"' : value.toString(), "${fieldType}").orElse(null);
      <#else>
        <#if genService.isCDEnum(attr.getSymbol().getType())>
      return ${fieldType}.valueOf(json.getMember("${fieldName}").${jsonStr});
        <#else>
      return json.getMember("${fieldName}").${jsonStr};
          </#if>
      </#if>
    </#if>

</#if>
