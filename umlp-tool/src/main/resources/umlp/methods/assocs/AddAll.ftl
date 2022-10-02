<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public void addAll${name?cap_first}s(int idx, Collection<${targetName}> l, NotificationScope scope)")}
if(idx < 0){
  throw new IllegalStateException("idx must be 0 or higher");
}

if(idx > ${name}.size()){
  throw new IllegalStateException("idx must be at most owns.size");
}

for(${targetName} o : l){
  add${name?cap_first}(idx++, o, scope);
}