<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.
-->
${tc.signature("name", "assocList")}
${cd4c.method("public Optional<${name}> build()")}

if (!buildFinished) {
  if (isBuildable()) {
<#list assocList as assoc>
<#assign name=assoc.getName()?cap_first>
    this.${name?uncap_first}.build();
</#list>
    <#-- TODO: add observer in Manager to call Manager.add -->
    //todo add after adding observer to class
    //${name?uncap_first}Observers.forEach(obs -> obs.notifyBuild(this)); //+ an initial notification
    buildFinished = true;
    return Optional.of(this);
  }
}
return Optional.empty();