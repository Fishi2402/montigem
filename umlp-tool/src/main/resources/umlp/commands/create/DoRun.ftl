<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "managerName", "packagename", "hasOCL")}
${cd4c.method("public StatusResult doAction()")}

${managerName}.addWithScope${classname}(${classname?uncap_first}, NotificationScope.LOCAL);
return StatusResult.ok(getId());
