<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected ${name} _doRegister${name}(${name} obj)")}

<#-- First one wins: keep the cached instance to guard against fetch races. -->
if (known${name}s.containsKey(obj.getGemId())) {
  return known${name}s.get(obj.getGemId());
}
if (!(obj).containsObserver(the${name}CommandObserver)) {
  (obj).addObserver(the${name}CommandObserver);
}
Log.trace("Adding known ${name?uncap_first} " + obj.getGemId(), LogUtil.getLoggerName());
known${name}s.put(obj.getGemId(), obj);
return obj;
