<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "getter")}
<#assign mcType = type.printType()>

${cd4c.method("public ${mcType} ${getter}()")}
return this.${name};