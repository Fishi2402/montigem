<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected String _serializeCommand(Command<any> command)")}

return this.commandDeSer.serialize(command);