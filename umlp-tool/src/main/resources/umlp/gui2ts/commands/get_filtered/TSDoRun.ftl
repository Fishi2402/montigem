<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "definition")}
${cd4c.method("public Promise<ListResult<Long, List<Long>>> doRun()")}

return ${definition}Manager.get${classname}List().then(v => ListResult.of(this.getId(), v.getIDs(), "java.lang.Long"));