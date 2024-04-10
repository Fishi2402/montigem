${tc.signature("domainVar", "domainVarType", "manager")}
${cd4c.method("protected void update${domainVarType}s()") }
${manager}.get${domainVarType}List().then(gemPromiseRes => {
  this.set${domainVar?cap_first}_internal(gemPromiseRes);
  ${tc.include("umlp.gui2java.InitDomainBody")}
});
