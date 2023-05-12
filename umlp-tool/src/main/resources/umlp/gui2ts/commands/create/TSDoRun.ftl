<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","definition")}
${cd4c.method("public StatusResult doRun()")}

${definition}Manager.add${classname}(this.${classname?uncap_first});
return StatusResult.ok(this.getId());