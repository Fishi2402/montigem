<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","sourcename","targetname","definition")}
${cd4c.method("public StatusResult doRun()")}

Optional<${sourcename}> sourceOpt = ${definition}Manager.${sourcename?uncap_first}Opt(gemId);
    if(sourceOpt.isPresent()){
        sourceOpt.get().remove${assoc?cap_first}ByIdSure(gemId${targetname});
        return StatusResult.ok(getId());
    } else {
        GeneralErrorCollector.reportSyncError();
        return StatusResult.error(getId());
    }