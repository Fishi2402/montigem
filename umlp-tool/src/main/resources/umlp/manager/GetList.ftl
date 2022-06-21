<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static Collection<${name}> get${name}List()")}

return getSingleton()._doGet${name}List();