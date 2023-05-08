<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","definition")}
${cd4c.method("public StatusResult doRun()")}
  if (${definition}Manager.delete${classname}(gemId)) {
    return StatusResult.ok(getId());
  } else {
    return StatusResult.error(getId());
  }