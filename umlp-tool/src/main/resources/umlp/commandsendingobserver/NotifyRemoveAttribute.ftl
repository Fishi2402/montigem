<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","attributename")}
${cd4c.method("public void notifyRemove${attributename?cap_first}(${classname} o, long gemId)")}

CommandManager.sendCommandToSubscribers(new ${classname}_remove${attributename?cap_first}(o.getGemId(), gemId));