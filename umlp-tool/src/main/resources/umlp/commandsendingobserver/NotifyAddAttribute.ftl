<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","name")}
${cd4c.method("public void notifyAdd${name?cap_first}(${classname} o, long gemId, int indexInList)")}

CommandManager.sendCommandToSubscribers(new ${classname}_add${name?cap_first}(o.getGemId(), gemId, indexInList));