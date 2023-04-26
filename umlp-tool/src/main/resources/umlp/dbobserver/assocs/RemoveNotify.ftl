<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className","assocName","assocSide", "otherClassName")}
${cd4c.method("public void notifyRemove${assocSide.name?cap_first}(${otherClassName} ${otherClassName?uncap_first}, long ${assocSide.getName()}GemId)")}
<#if assocSide.isLeft()>
  assoc${assocName?cap_first}DAO.remove${assocName?cap_first}(${otherClassName?uncap_first}.getGemId(), ${assocSide.getName()}GemId);
<#else>
  assoc${assocName?cap_first}DAO.remove${assocName?cap_first}(${assocSide.getName()}GemId, ${otherClassName?uncap_first}.getGemId());
</#if>
