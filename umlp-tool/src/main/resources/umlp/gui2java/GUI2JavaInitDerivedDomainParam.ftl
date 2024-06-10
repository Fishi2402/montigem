<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "initList")}
<#assign javaType = cdPrinter.printType(type)>

${cd4c.method("protected void ${init}()")}
this.${name} = ${manager}.get${javaType}Sure(-1);
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}