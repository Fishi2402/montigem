<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "manager")}
${cd4c.method("public Promise<ListResult<Long, List<Long>>> doRun()")}

return ${manager}.get${classname}List().then(v => ListResult.of(this.getId(), v.getIDs(), "java.lang.Long"));
