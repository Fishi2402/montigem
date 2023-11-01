<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainVar", "domainVarType", "manager")}
if(this.${domainVar} == null) {
console.log("Fetching ${domainVar}")
  ${manager}.get${domainVarType}List().then(gemPromiseRes => {
console.log("Got " + gemPromiseRes);
gemPromiseRes.get(0).then(gemPromiseRes2 => {
this.${domainVar} = gemPromiseRes2;
});

    ${tc.include("umlp.gui2java.InitDomainBody")}
  });
}
