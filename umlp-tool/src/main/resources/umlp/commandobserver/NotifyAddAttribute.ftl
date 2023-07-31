<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","type","assocName","isBidirectional", "isLeft", "otherType")}
${cd4c.method("public void notifyAdd${name?cap_first}(${otherType} o, ${type} attr, int indexInList)")}
<#if isBidirectional>
  <#if isLeft>
  CommandManager.executeCommand(new ${assocName?cap_first}_add(o.getGemId(), attr, indexInList, -1));
  <#else>
  CommandManager.executeCommand(new ${assocName?cap_first}_add(o.getGemId(), attr, -1, indexInList));
  </#if>
<#else>
  CommandManager.executeCommand(new ${otherType}_add${name?cap_first}(o.getGemId(), attr, indexInList));
</#if>
