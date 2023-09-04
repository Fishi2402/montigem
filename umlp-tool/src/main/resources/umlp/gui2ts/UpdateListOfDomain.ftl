${tc.signature("domainVar", "domainVarType", "manager")}
${cd4c.method("protected void update${domainVarType}s()") }
${manager}.get${domainVarType}List().then(gemPromiseRes => {
  this.${domainVar} = gemPromiseRes;
  ${tc.include("umlp.gui2java.InitDomainBody")}
});
