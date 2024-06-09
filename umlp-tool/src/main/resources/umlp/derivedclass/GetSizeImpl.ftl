<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "typeParameter", "calcSize")}

${cd4c.method("public int size${name?cap_first}()")}
return this.${calcSize.getName()}();