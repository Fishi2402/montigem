<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "className", "simpleAttributes", "gemAttributes", "gemListAttributes")}
${cd4c.method("public Optional<${className}> deserialize(any json)")}
  let builder: ${className}Builder = ${cdName}Manager.${className?uncap_first}Builder1(json["gemId"]);
  builder.setINTERNAL_deser_mode(true);
  <#if simpleAttributes?has_content || gemAttributes?has_content || gemListAttributes?has_content>
  builder
    <#list simpleAttributes as attr>
      .${attr}(this.deserialize${attr?cap_first}(json))
    </#list>
    <#list gemAttributes as attr>
      .${attr}Gem(this.deserialize${attr?cap_first}(json))
    </#list>
    <#list gemListAttributes as attr>
      .${attr}GemListIds(this.deserialize${attr?cap_first}(json))
    </#list>
  ;
  </#if>

  this.deserialize_Addons(builder, json);
  return builder.build();
