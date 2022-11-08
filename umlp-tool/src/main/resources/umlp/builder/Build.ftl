<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.
-->
${tc.signature("name", "managerName")}
${cd4c.method("public Optional<${name}> build()")}

if (!buildFinished) {
  if (isBuildable()) {
    <#-- TODO: add observer in Manager to call Manager.add -->
    ${name?uncap_first}Observers.forEach(obs -> obs.notifyBuild(this)); //+ an initial notification
    buildFinished = true;
    return Optional.of(this);
  }
}
return Optional.empty();