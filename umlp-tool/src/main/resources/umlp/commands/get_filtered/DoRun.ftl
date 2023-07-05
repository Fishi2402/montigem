<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "managerName")}
${cd4c.method("public ListResult<Long, List<Long>> doRun()")}

return ListResult.of(getId(), ${managerName}.get${classname}List().getIDs(), "java.lang.Long");
