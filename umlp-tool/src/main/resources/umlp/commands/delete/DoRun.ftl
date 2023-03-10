<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname","definition")}
${cd4c.method("public StatusResult doRun()")}

Optional<${classname}> ${classname?uncap_first} = ${definition}Manager.${classname?uncap_first}Opt(gemId);
if (${classname?uncap_first}.isPresent()) {
  ${definition}Manager.delete${classname}(${classname?uncap_first}.get());
  return StatusResult.ok(getId());
} else {
  return StatusResult.error(getId());
}