<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "managerName", "packagename")}
${cd4c.method("public ListResult<Long, List<Long>> doRun()")}
if(checkAccessPolicy()){
  return ListResult.of(getId(), ${managerName}.get${classname}List().getIDs(), "java.lang.Long");
}
else {
  return ListResult.error(getId(), "java.lang.Long");
}
