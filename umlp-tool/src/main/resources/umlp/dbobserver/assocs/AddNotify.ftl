<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className","assocName","assocSide", "otherClassName")}
${cd4c.method("public void notifyAdd${assocSide.getName()?cap_first}(${otherClassName} ${otherClassName?uncap_first}, long ${assocSide.getName()}GemId, int indexInList)")}
<#if assocSide.isLeft()>
  assoc${assocName?cap_first}DAO.add${assocName?cap_first}(${assocSide.getName()}GemId, ${otherClassName?uncap_first}.getGemId(), indexInList);
<#else>
  assoc${assocName?cap_first}DAO.add${assocName?cap_first}(${otherClassName?uncap_first}.getGemId(), ${assocSide.getName()}GemId, indexInList);
</#if>
