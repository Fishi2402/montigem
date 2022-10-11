<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static ${name}ErrorCollector getInstance()")}

if (instance == null) {
  init();
}
return instance;