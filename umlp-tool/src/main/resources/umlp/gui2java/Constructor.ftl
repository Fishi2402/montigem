<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "parameters", "domainAttributes", "attributes", "guiDSLPrinter", "isInputGui")}
${cd4c.constructor("public ${name}(${parameters})")}
<#list attributes as attribute>
    this.${attribute.getName()} = ${attribute.getName()};
</#list>
<#list domainAttributes as attribute>
    this.${attribute.getName()} = ${attribute.getName()};
    <#if isInputGui>
    // the page observes errors in ${guiDSLPrinter.prettyprint(attribute.getMCType())} object to display to user
    // TODO ? ${guiDSLPrinter.prettyprint(attribute.getMCType())}ErrorCollector.addErrorObserver(this);
    </#if>
</#list>

    init();
