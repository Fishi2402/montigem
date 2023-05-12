<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "sourcename","targetname")}
${cd4c.method("public StatusResult doRun()")}

let ${targetname}Changed: boolean = this.old${targetname?cap_first}GemId != this.${targetname}GemId;
let ${sourcename}Changed: boolean = this.old${sourcename?cap_first}GemId != this.${sourcename}GemId;

if(${targetname}Changed){
  return this.exchange${targetname?cap_first}();
}

if(${sourcename}Changed){
  return this.exchange${sourcename?cap_first}();
}

return StatusResult.error(this.getId());
