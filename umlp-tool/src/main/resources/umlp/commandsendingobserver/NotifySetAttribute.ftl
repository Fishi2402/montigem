<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","attributename","type")}
${cd4c.method("public void notifySet${attributename?cap_first}(${classname} ${classname?uncap_first}, ${type} ${attributename})")}

CommandManager.sendCommandToSubscribers(new ${classname}_set_${attributename}(${classname?uncap_first}.getGemId(), ${attributename}));