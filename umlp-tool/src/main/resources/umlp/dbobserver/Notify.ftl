<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","name","className","typeParameter")}
${cd4c.method("public void notifySet${name?cap_first}(${className} ${className?uncap_first}, ${typeParameter} oldValue, ${typeParameter} ${name?uncap_first})")}
  ${cdName}DAOFactory.get${className}DAO().update${name?cap_first}(${className?uncap_first}.getGemId(), ${name?uncap_first});

