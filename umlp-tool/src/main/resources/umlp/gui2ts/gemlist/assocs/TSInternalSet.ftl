<#--(c) https://github.com/MontiCore/monticore-->
${tc.signature("name")}
${cd4c.method("protected void _internal_setAndSync(int idx, long gemID)")}
if (this.buildFinished) {
  this.getOwner().then(v => v._internal_set${name?cap_first}(idx, gemID, NotificationScope.ALL));
}
else {
  this._internal_set(idx, gemID);
}