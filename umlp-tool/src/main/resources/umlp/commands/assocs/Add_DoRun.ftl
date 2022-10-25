<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourcename","targetname","definition")}
${cd4c.method("public StatusResult doRun()")}

Optional<${sourcename}> sourceOpt = ${definition}Manager.${sourcename?uncap_first}Opt(gemId);
if(sourceOpt.isEmpty()){
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(getId());
}

Optional<${targetname}> targetOpt = ${definition}Manager.${targetname?uncap_first}Opt(gemId${targetName});
if(targetOpt.isEmpty()){
  GeneralErrorCollector.reportSyncError();
  return StatusResult.error(getId());
}

sourceOpt.get().add${assoc?cap_first}(indexInList, targetOpt.get(), NotificationScope.LOCAL);
return StatusResult.ok(getId());
