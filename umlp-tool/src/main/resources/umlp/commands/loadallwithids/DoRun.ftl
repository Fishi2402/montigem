<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "domainPackage", "manager")}
${cd4c.method("protected ListResult<${classname}, List<${classname}>> doAction()")}
  ListResult<${classname}, List<${classname}>> result = ListResult.of(getId(),
      ${manager}.loadAll${classname}WithIDs(getGemIDsList()), "${domainPackage}.${classname}");
  return result;
