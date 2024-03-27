<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainName", "type", "name")}
// TODO extend?
  return event => {
    ${domainName}Manager.add${type}(${name}.build());
  };
