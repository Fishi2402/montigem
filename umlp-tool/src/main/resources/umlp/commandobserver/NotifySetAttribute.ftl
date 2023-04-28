<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","type","assocName","isBidirectional", "otherType")}
${cd4c.method("public void notifySet${name?cap_first}(${classname} p, ${type} o)")}

<#if isBidirectional>
// TODO: CommandManager.executeCommand(new ${assocName?cap_first}_set(gemId, o.getGemId()));
<#else>
CommandManager.executeCommand(new ${otherType}_set${name?cap_first}(p.getGemId(), o));
</#if>
