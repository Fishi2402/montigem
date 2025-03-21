<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "managerName", "packagename")}
${cd4c.method("public ListResult<Long, List<Long>> doAction()")}
  if (getFilter().isPresent()) {
    return ListResult.of(getId(), ${managerName}.getFiltered${classname}List(getFilter().get()).getIDs(), "java.lang.Long");
  }
  return ListResult.of(getId(), ${managerName}.get${classname}List().getIDs(), "java.lang.Long");