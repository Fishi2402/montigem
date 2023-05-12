<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected boolean _containsErrorObserver(${name}ErrorObserver observer)")}

return this.observers.includes(observer);