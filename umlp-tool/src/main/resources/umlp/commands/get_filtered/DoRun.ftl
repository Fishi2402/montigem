<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "definition")}
${cd4c.method("public ListResult<Long, List<Long>> doRun()")}

return ListResult.of(getId(), ${definition}Manager.get${classname}List().getIDs(), "java.lang.Long");