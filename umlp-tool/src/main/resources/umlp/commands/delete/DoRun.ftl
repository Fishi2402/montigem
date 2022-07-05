<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname")}
${cd4c.method("public StatusResult doRun()")}

FlensburgManager.delete${classname}(${classname?uncap_first});
return StatusResult.ok(getId());