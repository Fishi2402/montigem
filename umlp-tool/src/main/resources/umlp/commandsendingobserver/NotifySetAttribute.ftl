<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","attributename","type")}
${cd4c.method("public void notifySet${attributename?cap_first}(${classname} ${classname?uncap_first}, ${type} o)")}

CommandManager.sendCommandToSubscribers(new ${classname}_set${attributename?cap_first}(${classname?uncap_first}.getGemId(), o));