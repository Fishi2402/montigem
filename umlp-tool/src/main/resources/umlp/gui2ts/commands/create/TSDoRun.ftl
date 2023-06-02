<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","definition")}
${cd4c.method("public Promise<StatusResult> doRun()")}

return ${definition}Manager.add${classname}(this.${classname?uncap_first})
  .then(() => StatusResult.ok(this.getId()));