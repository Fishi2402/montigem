<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "initializationTypeName", "simpleAttributes")}
${cd4c.method("public Optional<${initializationTypeName}> deserialize(JsonObject json)")}
  ${initializationTypeName}Builder builder = new ${initializationTypeName?cap_first}Builder();
  <#if simpleAttributes?has_content>
  builder
    <#list simpleAttributes as attr>
      .${attr}(deserialize${attr?cap_first}(json))
    </#list>
  ;
  </#if>
  return builder.build();
