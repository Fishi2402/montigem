<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","classname","targetname","definition")}
${cd4c.method("public StatusResult doRun()")}

Optional<${classname}> ${classname?uncap_first}Opt = ${definition}Manager.${classname?uncap_first}Opt(gemId);
if(${classname?uncap_first}Opt.isEmpty()){
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(getId());
}

Optional<${targetname?cap_first}> ${assoc}Opt = ${definition}Manager.${targetname?uncap_first}Opt(gemId${assoc});
if(${assoc}Opt.isEmpty()){
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(getId());
}

${classname?uncap_first}Opt.get().add${assoc?cap_first}(indexInList, ${assoc}Opt.get(), NotificationScope.LOCAL);
return StatusResult.ok(getId());
