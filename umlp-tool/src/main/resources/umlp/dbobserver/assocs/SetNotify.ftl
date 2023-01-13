<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className","assocName","assocSideName")}
${cd4c.method("public void notifySet${assocName?cap_first}(${className} ${className?uncap_first}, long old${assocSideName?cap_first}GemId, long ${assocSideName}GemId, int indexInList)")}
  assoc${assocName?cap_first}DAO.update${assocSideName?cap_first}(${className?uncap_first}.getGemId(), old${assocSideName?cap_first}GemId, ${assocSideName}GemId, indexInList);


