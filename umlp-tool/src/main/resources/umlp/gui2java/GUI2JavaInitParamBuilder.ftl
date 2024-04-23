<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "builder")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

${cd4c.method("protected void ${builder}()")}
this.${name} = ${manager}.${type?uncap_first}Builder();
