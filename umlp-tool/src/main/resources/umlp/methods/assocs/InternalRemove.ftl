<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public void _internal_remove${name?cap_first}(java.util.Set<Integer> idxs, NotificationScope scope)")}
Map<Integer, Long> idxToId = new HashMap<>();
for(int idx : idxs){
  idxToId.put(idx, _${name}.getID(idx));
}

this._${name}._internal_remove(idxs);
check${name?cap_first}();
for(Map.Entry<Integer, Long> e : idxToId.entrySet()) {
    ${sourceName?uncap_first}Observers.forEach(obs -> obs.maybeNotifyRemove${name?cap_first}((${sourceName}) this, e.getValue(), e.getKey(), scope));
}
