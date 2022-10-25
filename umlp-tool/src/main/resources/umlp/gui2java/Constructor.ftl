<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "parameters", "domainAttributes", "attributes", "guiDSLPrinter")}
${cd4c.constructor("protected ${name}(${parameters})")}
<#list attributes as attribute>
    this.${attribute.getName()} = ${attribute.getName()};
</#list>
<#list domainAttributes as attribute>
    this.${attribute.getName()} = ${attribute.getName()};
    // the page observes errors in ${guiDSLPrinter.prettyprint(attribute.getMCType())} object to display to user
    ${guiDSLPrinter.prettyprint(attribute.getMCType())}ErrorCollector.addErrorObserver(this);
</#list>

    init();