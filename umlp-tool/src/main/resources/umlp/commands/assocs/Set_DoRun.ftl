<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "sourcename","targetname")}
${cd4c.method("public StatusResult doRun()")}

boolean ${targetname?uncap_first}Changed = old${targetname}GemId != ${targetname?uncap_first}GemId;
boolean ${sourcename?uncap_first}Changed = old${sourcename}GemId != ${sourcename?uncap_first}GemId;

if(${targetname?uncap_first}Changed){
  return exchange${targetname}();
}

if(${sourcename?uncap_first}Changed){
  return exchange${sourcename}();
}

return StatusResult.error(getId());
