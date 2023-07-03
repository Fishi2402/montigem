<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdName")}
${cd4c.method("public void init()")}

${cdName}TestContext.clear();
${cdName}Mock.init();
backend = ${cdName}TestContext.fromCurrentContext();
