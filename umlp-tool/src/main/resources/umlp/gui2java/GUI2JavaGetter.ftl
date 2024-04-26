<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "syncMethod")}
<#assign genHelper = glex.getGlobalVar("genHelper")>

${cd4c.method("public ${type.printType()} get${name?cap_first}()")}
<#-- this.${syncMethod.getName()}(); -->
return this.${name};