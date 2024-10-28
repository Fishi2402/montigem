<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","manager", "packagename", "hasOCL")}
${cd4c.method("public Promise<StatusResult> doRun()")}

return ${manager}.add${classname}2(this.${classname?uncap_first}, NotificationScope.LOCAL)
  .then(() => StatusResult.ok(this.getId()));
