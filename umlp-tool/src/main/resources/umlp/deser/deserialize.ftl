<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "attributes")}
${cd4c.method("public Optional<${className}> deserialize(String jsonString)")}
  JsonElement elem = JsonParser.parse(jsonString);
  JsonObject json = elem.getAsJsonObject();
  ${className}Builder builder = ${cdName}Manager.${className?uncap_first}Builder();
  <#-- TODO: replace deser_mode with DeSerBuilder subclass? -->
  builder.setINTERNAL_deser_mode(true);
  <#if attributes?has_content>
  builder
    <#list attributes as attr>
    <#-- TODO: use special setters for Gem and GemList-->
      .${attr.name}(deserialize${attr.name?cap_first}(json))
    </#list>
  ;
  </#if>

  deserialize_Addons(builder, json);
  return builder.build();
