<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainVar", "domainVarType", "manager")}
${manager}.get${domainVarType}List().then(gemPromiseRes => {
  this.${domainVar} = gemPromiseRes;
  ${tc.include("umlp.gui2java.InitDomainBody")}
});
