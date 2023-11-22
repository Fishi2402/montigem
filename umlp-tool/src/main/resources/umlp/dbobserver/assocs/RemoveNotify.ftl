<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","className","assocName","assocSide", "otherClassName")}
${cd4c.method("public void notifyRemove${assocSide.name?cap_first}(${otherClassName} ${otherClassName?uncap_first}, long ${assocSide.getName()}GemId, int indexInList)")}
<#if !assocSide.isLeft()>
    ${cdName}DAOFactory.getAssoc${assocName?cap_first}DAO().remove${assocName?cap_first}(${otherClassName?uncap_first}.getGemId(), ${assocSide.getName()}GemId);
<#else>
    ${cdName}DAOFactory.getAssoc${assocName?cap_first}DAO().remove${assocName?cap_first}(${assocSide.getName()}GemId, ${otherClassName?uncap_first}.getGemId());
</#if>
