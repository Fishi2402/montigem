<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","managerName", "packagename")}
${cd4c.method("public StatusResult doRun()")}

${packagename}.${classname}AccessPolicy accessPolicy = new ${packagename}.${classname}AccessPolicy();
if(accessPolicy.checkPermission(this)){
  if (${managerName}.delete${classname}(gemId, NotificationScope.LOCAL)) {
    return StatusResult.ok(getId());
  }
  else {
    return StatusResult.error(getId());
  }
}
else {
  return StatusResult.error(getId());
}
