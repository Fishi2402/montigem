<#--(c) https://github.com/MontiCore/monticore-->
${tc.signature("name")}
${cd4c.method("protected void _internal_removeAndSync(Set<Integer> idxs)")}
if (buildFinished) {
  getOwner()._internal_remove${name?cap_first}(idxs, NotificationScope.ALL);
}
else {
  _internal_remove(idxs);
}