<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","domainClass","name","initializationTypeName","typeParameter")}
${cd4c.method("public void notifySet${name?cap_first}(${initializationTypeName} ${initializationTypeName?uncap_first}, ${typeParameter} oldValue, ${typeParameter} ${name?uncap_first})")}
  ${cdName}DAOFactory.get${domainClass}DAO().update${name?cap_first}(${initializationTypeName?uncap_first}.getGemId(), ${name?uncap_first});

