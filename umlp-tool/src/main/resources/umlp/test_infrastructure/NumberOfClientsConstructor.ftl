<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initializationTypeName", "attr")}
${cd4c.constructor("public ${initializationTypeName}(${attr.getMCType().printType()} ${attr.name})")}
 this(ClientSetup.of(${attr.name}));
