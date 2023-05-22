<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public void _internal_add${name?cap_first}(int idx, List<Long> gemIDs, NotificationScope scope)")}
if(idx < 0){
  console.error("0x50002 idx must be 0 or higher");
}
if(idx > this._${name}.length){
  console.error("0x50003 idx must be at most owns.size");
}
//Discuss: does this need to be checked after each added object?
this._${name}._internal_add(idx, gemIDs);
let currentIdx: number = idx;
for(const gemID of gemIDs) {
  this.check${name?cap_first}();
  for(const obs of this.${sourceName?uncap_first}Observers) {
    obs.maybeNotifyAdd${name?cap_first}(<${sourceName}> this, gemID, currentIdx, scope);
  }
  currentIdx++;
}