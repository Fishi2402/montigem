<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("public long _doGetFreeGemId()")}
  let res: SimpleResult<number> = CommandManager.executeCommand<number>(new GetFreeGemIdRequest()).asSimpleResult();
  return res.get();
  // TODO: error handling