<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "initializationTypeName", "simpleAttributes", "gemAttributes", "gemListAttributes")}
${cd4c.method("public Optional<${initializationTypeName}> deserialize(JsonObject json)")}
  ${initializationTypeName}Builder builder = ${cdName}Manager.${initializationTypeName?uncap_first}BuilderFromId(json.getLongMember("gemId"));
  builder.setINTERNAL_deser_mode(true);
  <#if simpleAttributes?has_content || gemAttributes?has_content || gemListAttributes?has_content>
  builder
    <#list simpleAttributes as attr>
      .${attr}(deserialize${attr?cap_first}(json))
    </#list>
    <#list gemAttributes as attr>
      .${attr}Gem(deserialize${attr?cap_first}(json))
    </#list>
    <#list gemListAttributes as attr>
      .${attr}GemListIds(deserialize${attr?cap_first}(json))
    </#list>
  ;
  </#if>

  deserialize_Addons(builder, json);
  return builder.build();
