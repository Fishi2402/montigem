<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public boolean containsErrorObserver(${name}ErrorObserver observer)")}

return ${name}ErrorCollector.getInstance()._containsErrorObserver(observer);