<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","definition")}
${cd4c.method("public Promise<StatusResult> doRun()")}
  return ${definition}Manager.delete${classname}(this.gemId)
    .then(() => StatusResult.ok(this.getId()))
    .catch(() => StatusResult.error(this.getId()));