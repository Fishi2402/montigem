<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "attributename")}
${cd4c.method("public StatusResult doRun()")}

Optional<${classname?cap_first}> ${classname?uncap_first}Opt = FlensburgManager.${classname?uncap_first}Opt(gemId);
    if(${classname?uncap_first}Opt.isPresent()){
    ${classname?cap_first}ErrorCollector.report${attributename?cap_first}Invalid(${classname?uncap_first}Opt.get());
    }
    return StatusResult.ok(getId());