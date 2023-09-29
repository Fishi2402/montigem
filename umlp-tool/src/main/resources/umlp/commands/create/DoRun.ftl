<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "managerName", "packagename")}
${cd4c.method("public StatusResult doAction()")}

${managerName}.add${classname}(${classname?uncap_first}, NotificationScope.LOCAL);
return StatusResult.ok(getId());
