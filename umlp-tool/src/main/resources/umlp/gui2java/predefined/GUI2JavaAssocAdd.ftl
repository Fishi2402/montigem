<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "getter", "isList", "assoc")}
<#assign param = "val">

${cd4c.method("public Consumer<Void> assoc_add(${type} ${param})")}
return $ -> {
  <#if isList>
    ${getter}.add${assoc?cap_first}(${param});
  <#else>
    ${getter}.set${assoc?cap_first}(${param});
  </#if>
};

