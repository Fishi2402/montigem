<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "className")}
if(idx < 0){
  console.error("0x50002 idx must be 0 or higher");
}
if(idx > this.${name}.size()){
  console.error("0x50003 idx must be at most owns.size");
}

this.${name}.set(idx, o);
${className?uncap_first}Observers.forEach(obs => obs.maybeNotifySet${name?cap_first}(<${className}> this, idx, o.getGemId(), scope));