<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","isBirectional")}

<#if isBirectional>
  ${cd4c.method("public void notifyRemove${name?cap_first}(${classname} o, long gemId, int indexInList)")}

  CommandManager.sendCommandToSubscribers(new ${name?cap_first}_remove(gemId, o.getGemId(), indexInList, -1));
<#else>
  ${cd4c.method("public void notifyRemove${name?cap_first}(${classname} o, long gemId)")}

  CommandManager.sendCommandToSubscribers(new ${classname}_remove${name?cap_first}(o.getGemId(), gemId));
</#if>
