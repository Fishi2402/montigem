<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "type", "getter", "isList", "assoc")}
<#assign param = "val">

${cd4c.method("public Consumer<Void> ${name}(${type} ${param})")}
return $ -> {
  <#if isList>
    ${getter}.add${assoc?cap_first}2(${param});
  <#else>
    ${getter}.set${assoc?cap_first}1(${param});
  </#if>
};

