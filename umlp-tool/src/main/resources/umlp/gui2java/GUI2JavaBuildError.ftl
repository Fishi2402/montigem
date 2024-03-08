<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attr")}
<#assign name = attr.getName()>
${cd4c.method("public void build${name?cap_first}Error(IllegalStateException ex)")}
