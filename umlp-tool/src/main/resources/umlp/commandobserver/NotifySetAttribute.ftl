<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","type","assocName","isBidirectional", "otherType", "isLeftAssocSide")}
${cd4c.method("public void notifySet${name?cap_first}(${classname} p, ${type} oldValue, ${type} o)")}

<#if isBidirectional>
  <#if isLeftAssocSide>
    CommandManager.executeCommand(new ${assocName?cap_first}_set(oldValue, p.getGemId(), o, p.getGemId(), 0, 0));
  <#else>
    CommandManager.executeCommand(new ${assocName?cap_first}_set(p.getGemId(), oldValue, p.getGemId(), o, 0, 0));
  </#if>
<#else>
  CommandManager.executeCommand(new ${otherType}_set${name?cap_first}(p.getGemId(), o));
</#if>

