<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}
  if(known${name?cap_first}s.containsKey(gemId)){
    return Optional.ofNullable(known${name?cap_first}s.get(gemId));
  }
  SimpleResult<${name}> res = CommandManager.executeCommand(new ${name}_get(gemId)).asSimpleResult();
  if (res.isSuccessful()) {
    ${name} ${name?uncap_first} = res.get();
    if (!${name?uncap_first}.containsObserver(the${name}CommandSendingObserver)) {
      ${name?uncap_first}.addObserver(the${name}CommandSendingObserver);
    }
    Log.trace("Adding known ${name?uncap_first} " + gemId, LogUtil.getLoggerName());
    known${name}s.put(${name?uncap_first}.getGemId(), ${name?uncap_first});
    return Optional.of(${name?uncap_first});
  }
  else {
    return Optional.empty();
  }