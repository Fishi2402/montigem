<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainclass", "attributes")}
${cd4c.method("protected void init()")}

<#list attributes as a>
  ${tc.includeArgs("umlp.gui2ts.derivedclass.TSInitGetSimple", a, [domainclass])}
</#list>

