<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","definition", "isGem", "targettype")}
${cd4c.method("public StatusResult doRun()")}

Log.trace("Executing set_${attributename} to " + ${attributename}<#if isGem>GemId</#if> + " on ${classname?uncap_first} " + gemId, LogUtil.getLoggerName());
Optional<${classname}> ${classname?uncap_first}Opt = ${definition}Manager.${classname?uncap_first}Opt(gemId);
    if(${classname?uncap_first}Opt.isPresent()){
<#if isGem>
      ${classname?uncap_first}Opt.get().set${attributename?cap_first}Gem(${definition}Manager.create${targettype}Gem(${attributename}GemId), NotificationScope.LOCAL);
<#else>
      ${classname?uncap_first}Opt.get().set${attributename?cap_first}(${attributename}, NotificationScope.LOCAL);
</#if>
        return StatusResult.ok(getId());
    } else {
        GeneralErrorCollector.reportSyncError();
        return StatusResult.error(getId());
    }