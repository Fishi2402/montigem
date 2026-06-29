<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cmdName", "domainPackage")}
${cd4c.constructor("public ${cmdName}(List<Long> gemIDs)")}
  super("${domainPackage}.commands.${cmdName}");
  this.gemIDs = gemIDs;
