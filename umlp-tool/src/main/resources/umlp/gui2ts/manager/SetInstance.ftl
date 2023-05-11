<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected static void setInstance(${name}Manager instance)")}

${name}Manager.instance = instance;
${name}BaseClassObjectMapper.init();
${name}CommandObjectMapper.init();
