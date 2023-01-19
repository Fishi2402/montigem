<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public void _internal_set${name?cap_first}(int idx, long gemID, NotificationScope scope)")}
if(idx < 0){
  throw new IllegalStateException("idx must be 0 or higher");
}
if(idx > this._${name}.size()){
  throw new IllegalStateException("idx must be at most owns.size");
}
this._${name}._internal_set(idx, gemID);
check${name?cap_first}();
${sourceName?uncap_first}Observers.forEach(obs ->
  obs.maybeNotifySet${name?cap_first}((${sourceName}) this, idx, gemID, scope)
);