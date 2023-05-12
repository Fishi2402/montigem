<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected long _doGetFreeGemId()")}
  let res: SimpleResult<number> = CommandManager.executeCommand(new GetFreeGemIdRequest()).asSimpleResult();
  return res.get();
  // TODO: error handling