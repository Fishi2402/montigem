<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
${cd4c.method("protected final static Long getFreeGemId()")}
  if (url == null) {
    return -1l;
  }
  String response = sendCommand(new GetFreeGemIdRequest());
  Optional<CommandResult<?>> commandResult = CommandObjectMapper.deserializeResponse(response);
  if (commandResult.isPresent() && commandResult.get().isSimpleResult()) {
    Object o = ((SimpleResult) commandResult.get()).get();
    if (o instanceof Long) {
      return (Long) o;
    }
  }
  return -1l;