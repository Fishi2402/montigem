<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "sourcename","targetname")}
${cd4c.method("public StatusResult doAction()")}

boolean ${targetname}Changed = old${targetname?cap_first}GemId != ${targetname}GemId;
boolean ${sourcename}Changed = old${sourcename?cap_first}GemId != ${sourcename}GemId;

if(${targetname}Changed){
  return exchange${targetname?cap_first}();
}

if(${sourcename}Changed){
  return exchange${sourcename?cap_first}();
}

return StatusResult.error(getId());
