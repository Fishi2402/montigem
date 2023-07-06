<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "managerName")}
${cd4c.method("public StatusResult doRun()")}

${managerName}.add${classname}(${classname?uncap_first});
return StatusResult.ok(getId());
