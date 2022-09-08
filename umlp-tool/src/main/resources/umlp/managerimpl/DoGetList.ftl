<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected Collection<${name}> _doGet${name}List()")}

return DatabaseAccessManager.getAll(${name}.class);