<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected final static Long getFreeGemId()")}
  CommandResult<Long> response = sendCommandToServerAndReceiveResult(new GetFreeGemIdRequest());
  if (response.isSimpleResult() && ((SimpleResult) response).getLoadingState() == LoadingState.LOADED) {
    return ((SimpleResult<Long>) response).get();
  } else {
    return -1L;
  }