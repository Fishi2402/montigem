<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static ${name} getSingleton()")}

if (this.instance == null) {
  console.error("0x50005 Initialize this instance using a concrete implementation of ${name}.")
}
return this.instance;