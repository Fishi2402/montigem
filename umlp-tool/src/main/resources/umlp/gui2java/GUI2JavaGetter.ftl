<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name")}
<#assign mcType = type.printType()>

${cd4c.method("public ${mcType} get${name?cap_first}()")}
return this.${name};