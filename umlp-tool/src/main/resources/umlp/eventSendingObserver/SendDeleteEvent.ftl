<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type")}
${cd4c.method("public void notify${type}Deleted(Long gemId)")}
CommandManager.sendCommandToSubscribers(new ${type}_delete(gemId));
