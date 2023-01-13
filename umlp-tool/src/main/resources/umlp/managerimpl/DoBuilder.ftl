<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name")}
${cd4c.method("protected  ${name}Builder _do${name}Builder()")}
  ${name}Builder ${name}Builder = new ${name}Builder(getFreeGemId());
  ${name}Builder.addObserver(the${name}DbObserver);
  return ${name}Builder;