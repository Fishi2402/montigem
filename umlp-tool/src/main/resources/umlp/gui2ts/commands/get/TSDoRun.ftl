<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "packagename", "manager")}
${cd4c.method("public Promise<SimpleResult<${classname}>> doRun()")}

return ${manager}.${classname?uncap_first}Opt(this.gemId).then(${classname?uncap_first} => {
  if(${classname?uncap_first} != null) {
    return new SimpleResult<${classname}>().SimpleResult1(this.getId(), ${classname?uncap_first}, "${packagename}.${classname}");
  } else {
    return new SimpleResult<${classname}>().SimpleResult2(this.getId(), LoadingState.EMPTY, "${packagename}.${classname}");
  }
});
