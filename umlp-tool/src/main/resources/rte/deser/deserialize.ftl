<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "simpleAttributes")}
${cd4c.method("public Optional<${className}> deserialize(JsonObject json)")}
  ${className}Builder builder = new ${className?cap_first}Builder();
  <#if simpleAttributes?has_content>
  builder
    <#list simpleAttributes as attr>
      .${attr}(deserialize${attr?cap_first}(json))
    </#list>
  ;
  </#if>
  return builder.build();
