<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "fieldName", "fieldType", "jsonStr", "isList", "isOptional")}

<#if !jsonStr?has_content>
  ${cd4c.method("abstract protected ${fieldType} deserialize${fieldName?cap_first} (JsonObject json)")}
<#else>
  ${cd4c.method("protected ${fieldType} deserialize${fieldName?cap_first} (JsonObject json)")}

  <#if isList>${fieldType} result = new ArrayList<>();
    if(!json.hasMember("${fieldName}")) {
      return result;
    }
    for(de.monticore.symboltable.serialization.json.JsonElement e : json.getArrayMember("${fieldName}")) {
      result.add(e.${jsonStr});
    }
    return result;
  <#elseif isOptional>
    if(json.hasMember("${fieldName}")){
      return java.util.Optional.of(json.getMember("${fieldName}").${jsonStr});
    } else {
      return java.util.Optional.empty();
    }
  <#else>
    <#-- TODO: default values -->
    return json.getMember("${fieldName}").${jsonStr};
  </#if>

</#if>