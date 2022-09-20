<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "domainType", "domainName")}
${cd4c.constructor("protected ${name}(${domainType} ${domainName})")}
    this.${domainName} = ${domainName};
    // the page observes errors in ${domainType} object to display to user
    ${domainType}ErrorCollector.addErrorObserver(this);