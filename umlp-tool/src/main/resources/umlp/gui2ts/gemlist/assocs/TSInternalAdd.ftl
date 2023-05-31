<#--(c) https://github.com/MontiCore/monticore-->
${tc.signature("name")}
${cd4c.method("protected void _internal_addAndSync(int idx, List<Long> gemIDs)")}
if (this.buildFinished) {
  this.getOwner()._internal_add${name?cap_first}(idx, gemIDs, NotificationScope.ALL);
}
else {
  this._internal_add(idx, gemIDs);
}