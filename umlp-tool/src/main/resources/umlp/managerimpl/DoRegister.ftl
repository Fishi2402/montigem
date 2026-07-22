<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected ${name} _doRegister${name}(${name} obj)")}

if (known${name}s.containsKey(obj.getGemId())) {
  return known${name}s.get(obj.getGemId());
}
_doAddRequiredObserver${name}(obj);
known${name}s.put(obj.getGemId(), obj);
return obj;
