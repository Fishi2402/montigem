<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","manager", "packagename")}
${cd4c.method("public Promise<StatusResult> doRun()")}

return ${manager}.add${classname}(this.${classname?uncap_first})
  .then(() => StatusResult.ok(this.getId()));
