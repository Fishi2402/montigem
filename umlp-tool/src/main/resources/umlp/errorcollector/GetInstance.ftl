<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected static ${name}ErrorCollector getInstance()")}

if (instance == null) {
  init();
}
return instance;




