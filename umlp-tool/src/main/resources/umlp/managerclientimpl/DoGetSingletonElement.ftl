<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected ${name} _doGet${name}()")}
if (${name?uncap_first}Instance.isEmpty()) {
  CommandResult<${name}> commandResult = CommandManager.executeCommand(new ${name}_get(-1));
  if (commandResult.isSimpleResult()) {
    SimpleResult<${name}> res = commandResult.asSimpleResult();
    if (res.isSuccessful()) {
      ${name} ${name?uncap_first} = res.get();
      if (!(${name?uncap_first}).containsObserver(the${name}CommandObserver)) {
        (${name?uncap_first}).addObserver(the${name}CommandObserver);
      }
      Log.trace("Setting instance of ${name?uncap_first} " , LogUtil.getLoggerName());
      ${name?uncap_first}Instance = Optional.of(${name?uncap_first});
    }
  }
}
return ${name?uncap_first}Instance.get();
