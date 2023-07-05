<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","managerName")}
${cd4c.method("public StatusResult doRun()")}
  if (${managerName}.delete${classname}(gemId)) {
    return StatusResult.ok(getId());
  } else {
    return StatusResult.error(getId());
  }
