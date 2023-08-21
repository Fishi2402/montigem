<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className","assocName","assocSide", "otherClassName")}
<#assign isMult=genService.isList(assocSide) />
${cd4c.method("public void notifySet${assocSide.name?cap_first}(${otherClassName} ${otherClassName?uncap_first}, ${isMult?then('int idx,', '')} long ${assocSide.name}GemId)")}
<#if isMult>
  // TODO: Optimization, this might load ids we don't have loaded?
  long old${assocSide.name?cap_first}GemId = ${otherClassName?uncap_first}._internal_get${assocSide.name?cap_first}GemList().getID(idx);
<#else>
long old${assocSide.name?cap_first}GemId = ${otherClassName?uncap_first}.get${assocSide.name?cap_first}Gem().getGemId();
</#if>
assoc${assocName?cap_first}DAO.update${assocSide.name?cap_first}(${otherClassName?uncap_first}.getGemId(), old${assocSide.name?cap_first}GemId, ${assocSide.name}GemId);
