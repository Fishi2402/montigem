<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","managerName", "packagename")}
${cd4c.method("public StatusResult doAction()")}

if (${managerName}.delete${classname}(gemId, NotificationScope.LOCAL)) {
  return StatusResult.ok(getId());
}
else {
  return StatusResult.error(getId());
}
