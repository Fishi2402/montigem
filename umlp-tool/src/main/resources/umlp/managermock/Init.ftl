<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static ${name}Mock init()")}

${name}Mock i = new ${name}Mock();
${name}Manager.setInstance(i);
return i;
