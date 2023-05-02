<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","type","assocName","isBidirectional", "otherType")}
${cd4c.method("public void notifyRemove${name?cap_first}(${otherType} o, ${type} attr, int indexInList)")}
<#if isBidirectional>
  CommandManager.sendCommandToSubscribers(new ${assocName?cap_first}_remove(attr, o.getGemId(), indexInList, -1));
<#else>
  CommandManager.sendCommandToSubscribers(new ${otherType}_remove${name?cap_first}(o.getGemId(), attr));
</#if>
