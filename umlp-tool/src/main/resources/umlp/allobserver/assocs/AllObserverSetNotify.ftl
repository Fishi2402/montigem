<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","className","assocName","assocSide", "otherClassName")}
<#assign isMult=genService.isList(assocSide) />
  ${cd4c.method("public void notifySet${assocSide.name?cap_first}(${otherClassName} ${otherClassName?uncap_first}, ${isMult?then('int idx,', '')} long oldValue, long ${assocSide.name}GemId)")}
    this.consumer.accept(null);
