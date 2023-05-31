<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("public String _serializeResponse(CommandResult<any> result)")}

return this.resultDeSer.serialize(result);