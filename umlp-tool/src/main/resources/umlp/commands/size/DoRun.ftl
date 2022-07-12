<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("classname", "definition")}
${cd4c.method("public SimpleResult<Integer> doRun()")}

return new SimpleResult<>(getId(), ${definition}Manager.get${classname}List().size(), "java.lang.Integer");
