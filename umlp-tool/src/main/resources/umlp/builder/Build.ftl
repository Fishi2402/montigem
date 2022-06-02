<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The build method is generated to allow object creation.
-->
${tc.signature("name", "managerName")}
${cd4c.method("public Optional<${name}> build()")}

if (!buildFinished) {
  if (validator.buildingCheck(this)) {
    ${managerName}.add${name}(this);
    notifyObservers();      //+ an initial notification
    buildFinished = true;
    return Optional.of(this);
  }
}
return Optional.empty();



