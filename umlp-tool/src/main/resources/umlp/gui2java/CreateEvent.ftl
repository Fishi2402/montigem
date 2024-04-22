<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainName", "type", "name")}
// TODO: use passed argument instead of getter!
return event -> {
  ${domainName}Manager.add${type}(${name}.build());
};
