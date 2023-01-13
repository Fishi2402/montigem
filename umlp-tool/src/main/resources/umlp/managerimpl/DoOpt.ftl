<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected Optional<${name}> _do${name}Opt(long gemId)")}
  if(known${name?cap_first}s.containsKey(gemId)){
    return Optional.ofNullable(known${name?cap_first}s.get(gemId));
  }
  Optional<${name}> res = ${name?uncap_first}DAO.select${name}(gemId);
  if(res.isPresent()){
    ${name} h = res.get();
    if(!h.containsObserver(the${name}DbObserver)){
      h.addObserver(the${name}DbObserver);
    }
      known${name}s.put(h.getGemId(), h);
  }
  return res;
