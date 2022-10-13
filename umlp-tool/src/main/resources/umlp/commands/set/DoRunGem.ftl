<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "assoc","classname","target","definition")}
${cd4c.method("public StatusResult doRun()")}

Optional<${classname}> ${classname?uncap_first}Opt = ${definition}Manager.${classname?uncap_first}Opt(gemId);
    if(${classname?uncap_first}Opt.isPresent()){
        ${classname?uncap_first}Opt.get().set${assoc?cap_first}Gem(${definition}Manager.create${target}Gem(gemId${assoc}), NotificationScope.LOCAL);
        return StatusResult.ok(getId());
    } else {
        GeneralErrorCollector.reportSyncError();
        return StatusResult.error(getId());
    }