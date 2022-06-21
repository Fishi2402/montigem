<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("public static ${name} getSingleton()")}

if (instance == null) {
  throw new UnsupportedOperationException("Initialize this instance using a concrete implementation of ${name}.");
}
return instance;





