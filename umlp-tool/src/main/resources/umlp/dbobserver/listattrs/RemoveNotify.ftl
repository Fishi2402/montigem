<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName", "name","classDefiningAttrName", "className","type")}
<#assign attrName = classDefiningAttrName?uncap_first/>
${cd4c.method("public void notifyRemove${name?cap_first}(${classDefiningAttrName} ${attrName}, ${type} arg, int idx)")}
// TODO this is only a Hotfix an must be improved later -> goal: do not update the whole list

  var listToUpdate = ${cdName}DAOFactory.get${className?cap_first}DAO().select${name?cap_first}(${attrName}.getGemId());
  listToUpdate.remove(idx);
  ${cdName}DAOFactory.get${className?cap_first}DAO().update${name?cap_first}(
    ${attrName}.getGemId(),
    listToUpdate
  );
