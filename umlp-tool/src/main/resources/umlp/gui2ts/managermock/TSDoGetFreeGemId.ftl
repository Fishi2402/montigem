<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domain")}
${cd4c.method("public Promise<Long> _doGetFreeGemId()")}

return Promise.resolve(${domain}Mock.freeGemId++);
