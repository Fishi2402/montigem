<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected ${name} _doRegister${name}(${name} obj)")}

if (${name?uncap_first}s.containsKey(obj.getGemId())) {
  return ${name?uncap_first}s.get(obj.getGemId());
}
if (!(obj).containsObserver(the${name}CommandObserver)) {
  (obj).addObserver(the${name}CommandObserver);
}
${name?uncap_first}s.put(obj.getGemId(), obj);
return obj;
