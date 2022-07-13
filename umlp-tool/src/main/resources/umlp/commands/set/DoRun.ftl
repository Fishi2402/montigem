<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "attributename","classname","definition")}
${cd4c.method("public StatusResult doRun()")}

Log.trace("Executing set_${attributename} to " + ${attributename} + " on ${classname?uncap_first} " + gemId, LogUtil.getLoggerName());
Optional<${classname}> ${classname?uncap_first}Opt = ${definition}Manager.${classname?uncap_first}Opt(gemId);
    if(${classname?uncap_first}Opt.isPresent()){
        ${classname?uncap_first}Opt.get().set${attributename?cap_first}(
            <#if attributename?starts_with("opt")>Optional.ofNullable(${attributename})
            <#elseif attributename?starts_with("list")>Collections.singletonList(${attributename})
            <#else>${attributename}</#if>
            , NotificationScope.LOCAL);
        return StatusResult.ok(getId());
    } else {
        GeneralErrorCollector.reportSyncError();
        return StatusResult.error(gemId);
    }