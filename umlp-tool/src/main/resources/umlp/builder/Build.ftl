<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.
-->
${tc.signature("name", "managerName", "assocList")}
${cd4c.method("public Optional<${name}> build()")}

if (!buildFinished) {
  if (isBuildable()) {
<#list assocList as assoc>
    this._${assoc.getName()}.build();
</#list>
    if(!INTERNAL_deser_mode) {
      ${managerName}.add${name}(this);
    }
    ${name?uncap_first}Observers.forEach(obs -> obs.notifyBuild(this)); //+ an initial notification
    buildFinished = true;
    return Optional.of(this);
  }
}
return Optional.empty();