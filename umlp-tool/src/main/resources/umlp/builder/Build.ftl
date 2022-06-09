<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.
-->
${tc.signature("name", "managerName")}
${cd4c.method("public Optional<${name}> build()")}

if (!buildFinished) {
  if (isBuildable()) {
    ${managerName}.add${name}(this);
    ${name?uncapFirst}Observers.forEach(obs -> obs.${name?uncapFirst}Build(this)); //+ an initial notification
    buildFinished = true;
    return Optional.of(this);
  }
}
return Optional.empty();



