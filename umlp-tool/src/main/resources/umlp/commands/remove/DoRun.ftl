<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","classname","targetname","definition")}
${cd4c.method("public StatusResult doRun()")}

Optional<${classname}> ${classname?uncap_first}Opt = ${definition}Manager.${classname?uncap_first}Opt(gemId);
    if(${classname?uncap_first}Opt.isPresent()){
        ${classname?uncap_first}Opt.get().remove${assoc?cap_first}ByIdSure(gemId${assoc});
        return StatusResult.ok(getId());
    } else {
        GeneralErrorCollector.reportSyncError();
        return StatusResult.error(getId());
    }