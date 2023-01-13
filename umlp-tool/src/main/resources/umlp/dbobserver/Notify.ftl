<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","className","typeParameter")}
${cd4c.method("public void notifySet${name?cap_first}(${className} ${className?uncap_first}, ${typeParameter} ${name?uncap_first})")}
  dao.update${name?cap_first}(${className?uncap_first}.getGemId(), ${name?uncap_first});

