<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainVar", "domainVarType", "manager")}
${manager}.get${domainVarType}Sure(this.${domainVar}RoutingId).then(gemPromiseRes => {
  this.${domainVar} = gemPromiseRes;
  ${tc.include("umlp.gui2java.InitDomainBody")}
});
