<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("Promise<${name}> _doGet${name}()")}

return Promise.resolve(new ${name}ClientImpl());
