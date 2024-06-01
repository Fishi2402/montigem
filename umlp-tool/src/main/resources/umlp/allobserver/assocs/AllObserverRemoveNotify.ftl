<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName","className","assocName","assocSide", "otherClassName")}
${cd4c.method("public void notifyRemove${assocSide.name?cap_first}(${otherClassName} ${otherClassName?uncap_first}, long ${assocSide.getName()}GemId, int indexInList)")}
  this.consumer.accept(null);
