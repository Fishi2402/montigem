<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "targetName", "sourceName")}

${cd4c.method("public void _internal_remove${name?cap_first}(java.util.Set<Integer> idxs, NotificationScope scope)")}
Set<Long> ids = idxs.stream()
    .map(_${name}::getID)
    .collect(java.util.stream.Collectors.toSet());
this._${name}._internal_remove(idxs);
check${name?cap_first}();
for(long gemID : ids) {
    ${sourceName?uncap_first}Observers.forEach(obs -> obs.maybeNotifyRemove${name?cap_first}((${sourceName}) this, gemID, scope));
}