<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static ${name}ErrorCollector getInstance()")}

if (this.instance == null) {
  this.init();
}
return this.instance;