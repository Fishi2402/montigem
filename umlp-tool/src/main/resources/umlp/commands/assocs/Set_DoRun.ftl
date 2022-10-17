<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourcename","targetname","definition")}
${cd4c.method("public StatusResult doRun()")}

Optional<${sourcename}> sourceOpt = ${definition}Manager.${sourcename?uncap_first}Opt(gemId);
    if(sourceOpt.isPresent()){
        sourceOpt.get().set${assoc?cap_first}Gem(${definition}Manager.create${targetname}Gem(gemId${targetname}), NotificationScope.LOCAL);
        return StatusResult.ok(getId());
    } else {
        GeneralErrorCollector.reportSyncError();
        return StatusResult.error(getId());
    }