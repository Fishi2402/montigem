<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className","assocName","assocSide")}
${cd4c.method("public void notifyRemove${assocName?cap_first}(${className} ${className?uncap_first}, long ${assocSide.getName()}GemId, int indexInList)")}
<#if assocSide.isLeft()>
  assoc${assocName?cap_first}DAO.remove${assocName?cap_first}(${className?uncap_first}.getGemId(), ${assocSide.getName()}GemId, indexInList);
<#else>
  assoc${assocName?cap_first}DAO.remove${assocName?cap_first}(${assocSide.getName()}GemId, ${className?uncap_first}.getGemId(), indexInList);
</#if>
