<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "className")}
if(idx < 0){
  throw new IllegalStateException("idx must be 0 or higher");
}
if(idx > ${name}.size()){
  throw new IllegalStateException("idx must be at most owns.size");
}

this.${name}.set(idx, o);
${className}Observers.forEach(obs -> obs.maybeNotifySet${name}((${className}) this, idx, o.getGemId(), scope));

