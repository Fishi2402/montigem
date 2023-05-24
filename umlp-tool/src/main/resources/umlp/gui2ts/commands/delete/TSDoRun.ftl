<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","definition")}
${cd4c.method("public StatusResult doRun()")}
  if (${definition}Manager.delete${classname}(this.gemId)) {
    return StatusResult.ok(this.getId());
  } else {
    return StatusResult.error(this.getId());
  }