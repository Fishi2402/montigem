<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
${cd4c.method("public void notify${type}Added(${type} o)")}
CommandManager.sendCommandToSubscribers(new ${type}_create(o));
