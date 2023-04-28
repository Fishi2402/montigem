<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","type","assocName","isBidirectional", "otherType")}

<#if isBidirectional>
  ${cd4c.method("public void notifyRemove${name?cap_first}(${otherType} o, long gemId, int indexInList)")}

  CommandManager.executeCommand(new ${assocName?cap_first}_remove(gemId, o.getGemId(), indexInList, -1));
<#else>
  ${cd4c.method("public void notifyRemove${name?cap_first}(${otherType} o, ${type} attr)")}
  CommandManager.executeCommand(new ${otherType}_remove${name?cap_first}(o.getGemId(), attr));
</#if>
