<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainName", "type", "name")}
// TODO: use passed argument instead of getter!
return event -> {
  if(!get${name?cap_first}().isBuildFinished()){
    get${name?cap_first}().build();
  }
};
