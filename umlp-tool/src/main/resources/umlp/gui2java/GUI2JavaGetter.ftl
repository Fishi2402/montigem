<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name")}

${cd4c.method("public ${type} get${name?cap_first}()")}
return this.${name};