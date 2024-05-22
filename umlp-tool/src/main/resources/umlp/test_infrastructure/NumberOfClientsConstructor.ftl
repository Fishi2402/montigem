<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "attr")}
${cd4c.constructor("public ${className}(${attr.getMCType().printType()} ${attr.name})")}
 this(ClientSetup.of(${attr.name}));
