<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

${cd4c.method("public ${type.printType()} get${name?cap_first}()")}
return this.${name};