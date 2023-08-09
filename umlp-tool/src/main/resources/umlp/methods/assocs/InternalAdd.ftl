<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "otherSideName", "targetName", "sourceName", "managerName")}

${cd4c.method("public void _internal_add${name?cap_first}(int idx, List<Long> gemIDs, NotificationScope scope)")}
if(idx < 0){
  throw new IllegalStateException("idx must be 0 or higher");
}
if(idx > _${name}.size()){
  throw new IllegalStateException("idx must be at most ${name}.size");
}
//Discuss: does this need to be checked after each added object?
this._${name}._internal_add(idx, gemIDs);
int currentIdx = idx;
for(long gemID : gemIDs) {
  check${name?cap_first}();

  for(${sourceName}Observer obs : ${sourceName?uncap_first}Observers) {
    obs.maybeNotifyAdd${name?cap_first}((${sourceName}) this, gemID, currentIdx, scope);
  }
  currentIdx++;
}
