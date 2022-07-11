<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","definition")}
${cd4c.method("public StatusResult doRun()")}

${definition}Manager.add${classname}(${classname?uncap_first});
return StatusResult.ok(getId());