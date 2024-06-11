<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "typeParameter", "calc")}

${cd4c.method("public ${typeParameter} get${name?cap_first}()")}
return this.${calc.getName()}();