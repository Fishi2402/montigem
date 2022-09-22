<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "domainType", "domainName", "isDomainType")}
${cd4c.constructor("protected ${name}(${domainType} ${domainName})")}
    this.${domainName} = ${domainName};

<#if isDomainType>
    // the page observes errors in ${domainType} object to display to user
    ${domainType}ErrorCollector.addErrorObserver(this);
</#if>
    init();