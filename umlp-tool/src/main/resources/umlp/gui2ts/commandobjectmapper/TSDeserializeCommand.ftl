<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected Optional<Command<any>> _deserializeCommand(String cmdStr)")}

return this.commandDeSer.deserialize(cmdStr);