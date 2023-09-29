<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","type","assocName","isBidirectional", "isLeft", "otherType")}
${cd4c.method("public void notifyRemove${name?cap_first}(${otherType} o, ${type} attr, int indexInList)")}
<#if isBidirectional>
  <#if isLeft>
  CommandManager.sendCommandToSubscribers(new ${assocName?cap_first}_remove(o.getGemId(), attr, indexInList, -1));
  <#else>
  CommandManager.sendCommandToSubscribers(new ${assocName?cap_first}_remove(o.getGemId(), attr, -1, indexInList));
  </#if>
<#else>
  CommandManager.sendCommandToSubscribers(new ${otherType}_remove${name?cap_first}(o.getGemId(), attr, indexInList));
</#if>
