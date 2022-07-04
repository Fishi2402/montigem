<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname")}
${cd4c.method("public StatusResult doRun()")}

FlensburgManager.add${classname?uncap_first}(${classname?uncap_first});
return StatusResult.ok(getId());