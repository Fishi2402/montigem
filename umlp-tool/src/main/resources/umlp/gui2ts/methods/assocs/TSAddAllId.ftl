<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName")}

${cd4c.method("public void addAll${name?cap_first}s1(int idx, List<${targetName}> l, NotificationScope scope)")}
if(idx < 0){
  console.error("0x50002 idx must be 0 or higher");
}

if(idx > this._${name}.size()){
  console.error("0x50003 idx must be at most owns.size");
}

const list: number[] = [];
for (let s of l) {
  let id: number = s.getGemId();
  list.push(id);
}
this._internal_add${name?cap_first}(idx, list, scope);
