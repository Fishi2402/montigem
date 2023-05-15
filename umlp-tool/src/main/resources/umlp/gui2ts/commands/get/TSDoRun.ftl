<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packagename", "definition")}
${cd4c.method("public SimpleResult<${classname}> doRun()")}

let ${classname?uncap_first}: ${classname} = ${definition}Manager.${classname?uncap_first}Opt(this.gemId);
    if(${classname?uncap_first} != null){
    return new SimpleResult<>(this.getId(), ${classname?uncap_first}, "${packagename}.${classname}");
    } else {
    return new SimpleResult<>(this.getId(), LoadingState.EMPTY, "${packagename}.${classname}");
    }