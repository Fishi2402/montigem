<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","type","assocName","isBidirectional", "otherType", "isLeftAssocSide")}
${cd4c.method("public void notifySet${name?cap_first}(${classname} p, ${type} oldValue, ${type} o)")}

<#if isBidirectional>
// TODO: indices needed
<#--
<#if isLeftAssocSide>
CommandManager.sendCommandToSubscribers(new ${assocName?cap_first}_set(p.getGemId(), p.getGemId(), oldValue, o, 0, 0));
<#else>
CommandManager.sendCommandToSubscribers(new ${assocName?cap_first}_set(oldValue, o, p.getGemId(), p.getGemId(), 0, 0));
</#if-->
<#else>
CommandManager.sendCommandToSubscribers(new ${otherType}_set${name?cap_first}(p.getGemId(), o));
</#if>
