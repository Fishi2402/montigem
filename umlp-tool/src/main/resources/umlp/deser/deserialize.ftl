<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "simpleAttributes", "gemAttributes", "gemListAttributes")}
${cd4c.method("public Optional<${className}> deserialize(String jsonString)")}
  JsonElement elem = JsonParser.parse(jsonString);
  JsonObject json = elem.getAsJsonObject();
  ${className}DeSerBuilder builder = ${cdName}Manager.${className?uncap_first}DeSerBuilder();
  <#if simpleAttributes?has_content || gemAttributes?has_content || gemListAttributes?has_content>
  builder
    <#list simpleAttributes as attr>
      .${attr.name}(deserialize${attr.name?cap_first}(json))
    </#list>
    <#list gemAttributes as attr>
      .${attr.name}Gem(deserialize${attr.name?cap_first}(json))
    </#list>
    <#list gemListAttributes as attr>
      .${attr.name}GemListIds(deserialize${attr.name?cap_first}(json))
    </#list>
  ;
  </#if>

  deserialize_Addons(builder, json);
  return builder.build();
