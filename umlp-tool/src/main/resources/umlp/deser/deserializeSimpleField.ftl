<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName", "fieldType", "jsonStr", "isList", "innerType", "isOptional", "useDeSer", "objectMapperName")}
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
      result.add(${objectMapperName}.<${innerType}>deserialize(e.getAsJsonString().getValue(), "${innerType}").orElse(null));
      <#else>
      result.add(e.${jsonStr});
      </#if>
      }
      return result;
    <#elseif isOptional>
      if(json.hasMember("${fieldName}")){
      <#if useDeSer>
      return java.util.Optional.of(${objectMapperName}.<${innerType}>deserialize(json.getMember("${fieldName}").getAsJsonString().getValue(), "${innerType}")).orElse(null);
      <#else>
      return java.util.Optional.of(json.getMember("${fieldName}").${jsonStr});
      </#if>
      } else {
      return java.util.Optional.empty();
      }
    <#else>
    <#-- TODO: default values -->
      <#if useDeSer>
      return  ${objectMapperName}.<${fieldType}>deserialize(json.getMember("${fieldName}").getAsJsonString().getValue(), "${fieldType}").orElse(null);
      <#else>
      return json.getMember("${fieldName}").${jsonStr};
      </#if>
    </#if>

</#if>