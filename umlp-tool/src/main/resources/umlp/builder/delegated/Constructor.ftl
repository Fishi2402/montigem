<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "delType")}
${cd4c.constructor("public ${name}(${delType} delegate)")}

super(delegate.getGemId());
this.delegate = delegate;
