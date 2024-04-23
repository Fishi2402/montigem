<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getter")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

${cd4c.method("public ${type} ${getter}()")}
return this.${name};