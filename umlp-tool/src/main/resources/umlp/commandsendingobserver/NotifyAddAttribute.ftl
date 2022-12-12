<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","isBirectional")}
${cd4c.method("public void notifyAdd${name?cap_first}(${classname} o, long gemId, int indexInList)")}

<#if isBirectional>
CommandManager.sendCommandToSubscribers(new ${name?cap_first}_add(gemId, o.getGemId(), indexInList, -1));
<#else>
CommandManager.sendCommandToSubscribers(new ${classname}_add${name?cap_first}(o.getGemId(), gemId, indexInList));
</#if>