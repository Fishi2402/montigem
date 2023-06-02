<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("public Promise<Long> _doGetFreeGemId()")}
  // TODO: error handling
  return CommandManager.executeCommand<number>(new GetFreeGemIdRequest()).then(v => {
    return v.asSimpleResult().get()
  });