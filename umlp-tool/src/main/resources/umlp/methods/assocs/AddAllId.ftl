<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void addAll${name?cap_first}s(int idx, List<${targetName}> l, NotificationScope scope)")}
if(idx < 0){
  throw new IllegalStateException("idx must be 0 or higher");
}

if(idx > _${name}.size()){
  throw new IllegalStateException("idx must be at most owns.size");
}

List<Long> list = new ArrayList<>();
  for (${targetName} s : l) {
  Long id = s.getGemId();
  list.add(id);
}
this._internal_add${name?cap_first}(idx, list, scope);
