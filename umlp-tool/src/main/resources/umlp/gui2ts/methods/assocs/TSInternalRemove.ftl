<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public void _internal_remove${name?cap_first}(java.util.Set<Integer> idxs, NotificationScope scope)")}
const idxToId: Map<number, number> = new Map();
for(const idx of idxs){
  idxToId.set(idx, this._${name}.getID1(idx));
}

this._${name}._internal_remove(idxs);
this.check${name?cap_first}();
for(const e of idxToId.entries()) {
    this.${sourceName?uncap_first}Observers.forEach(obs => obs.maybeNotifyRemove${name?cap_first}(<${sourceName}> this, e[1], e[0], scope));
}
