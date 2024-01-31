<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name","className", "type")}
${cd4c.method("public void notifyRemove${name?cap_first}(${className} ${className?uncap_first}, ${type} arg, int idx)")}

// TODO this is only a Hotfix an must be improved later -> goal: do not update the whole list

  var listToUpdate = ${cdName}DAOFactory.get${className?cap_first}DAO().select${name?cap_first}(${className?uncap_first}.getGemId());
  listToUpdate.remove(idx);
  ${cdName}DAOFactory.get${className?cap_first}DAO().update${name?cap_first}(
    ${className?uncap_first}.getGemId(),
    listToUpdate
  );
