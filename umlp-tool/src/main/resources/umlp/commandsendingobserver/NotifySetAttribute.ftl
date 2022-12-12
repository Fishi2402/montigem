<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name","type","isBirectional")}
${cd4c.method("public void notifySet${name?cap_first}(${classname} p, ${type} o)")}

CommandManager.sendCommandToSubscribers(new ${classname}_set${name?cap_first}(p.getGemId(), o));