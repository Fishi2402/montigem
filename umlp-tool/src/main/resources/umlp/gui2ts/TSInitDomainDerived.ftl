<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainVar", "domainVarType", "manager")}
if(this.${domainVar} === undefined) {
${manager}.get${domainVarType}Sure(-42).then(gemPromiseRes => {
this.${domainVar} = gemPromiseRes;
${tc.include("umlp.gui2java.InitDomainBody")}
});
}
