<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superCdDeSers")}
${cd4c.method("protected Optional<Command<Object>> _deserializeCommand(String cmdStr)")}

return commandDeSer.deserialize(cmdStr)
<#list superCdDeSers as tmp>
  .or(() -> ${tmp}.deserialize(cmdStr))
</#list>
.or(() -> { // Log a warning after no cdDeser was able to find the command
  // TODO: Move this JsonParser.parseJsonObject in front of everything?
  de.monticore.symboltable.serialization.json.JsonObject jsonObject = de.monticore.symboltable.serialization.JsonParser.parseJsonObject(cmdStr);
  if (jsonObject.hasStringMember("commandType")) {
    Log.warn("Can not find deSer for commandType " + jsonObject.getStringMember("commandType"));
  } else {
    Log.warn("Can not find deSer for unknown command format");
  }
  return Optional.empty();
});
