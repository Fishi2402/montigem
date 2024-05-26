<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "getter", "isList", "assoc")}
<#assign param = "val">

${cd4c.method("public Consumer<Void> ${name}(${type} ${param})")}
return $ -> {
  <#if isList>
    ${getter}.remove${assoc?cap_first}(${param});
  <#else>
    ${getter}.set${assoc?cap_first}Absent();
  </#if>
};