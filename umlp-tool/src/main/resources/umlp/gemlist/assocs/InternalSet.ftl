<#--(c) https://github.com/MontiCore/monticore-->
${tc.signature("name")}
${cd4c.method("protected void _internal_setAndSync(int idx, long gemID)")}
if (buildFinished) {
  getOwner()._internal_set${name?cap_first}(idx, gemID, NotificationScope.ALL);
}
else {
  this._internal_set(idx, gemID);
}