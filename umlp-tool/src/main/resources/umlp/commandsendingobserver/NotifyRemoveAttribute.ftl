<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","type","assocName","isBidirectional")}

<#if isBidirectional>
  ${cd4c.method("public void notifyRemove${name?cap_first}(${classname} o, long gemId, int indexInList)")}

  CommandManager.sendCommandToSubscribers(new ${assocName?cap_first}_remove(gemId, o.getGemId(), indexInList, -1));
<#else>
  ${cd4c.method("public void notifyRemove${name?cap_first}(${classname} o, ${type} attr)")}

  CommandManager.sendCommandToSubscribers(new ${classname}_remove${name?cap_first}(o.getGemId(), attr));
</#if>
