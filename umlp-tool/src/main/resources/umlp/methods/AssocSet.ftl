<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "className")}
if(idx < 0){
  throw new IllegalStateException("idx must be 0 or higher");
}
if(idx > ${name}.size()){
  throw new IllegalStateException("idx must be at most owns.size");
}

this.${name}.set(idx, o);
${className?uncap_first}Observers.forEach(obs -> obs.maybeNotifySet${name?cap_first}((${className}) this, idx, o.getGemId(), scope));