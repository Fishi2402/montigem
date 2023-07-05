<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","manager")}
${cd4c.method("public Promise<StatusResult> doRun()")}
  return ${manager}.delete${classname}(this.gemId)
    .then(() => StatusResult.ok(this.getId()))
    .catch(() => StatusResult.error(this.getId()));
