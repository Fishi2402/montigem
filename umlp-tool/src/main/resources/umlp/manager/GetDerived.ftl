<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static ${name} get${name}()")}

return getSingleton()._doGet${name}();