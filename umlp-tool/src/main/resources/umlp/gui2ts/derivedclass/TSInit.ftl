<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainclass", "attributes", "singleAssoc", "listAssoc")}
${cd4c.method("protected void init()")}

// attributes
<#list attributes as a>
  ${tc.includeArgs("umlp.gui2ts.derivedclass.TSInitGetSimple", a, [domainclass])}
</#list>
// assoc
<#list singleAssoc as a>
    ${tc.includeArgs("umlp.gui2ts.derivedclass.TSInitGetSimple", a, [domainclass])}
</#list>
// assoc
<#list listAssoc as a>
    ${tc.includeArgs("umlp.gui2ts.derivedclass.TSInitGetList", a, [domainclass])}
</#list>


