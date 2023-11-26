<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected ${name}DAOInterface _get${name}DAO()")}

return instance.${name?uncap_first}DAO;
