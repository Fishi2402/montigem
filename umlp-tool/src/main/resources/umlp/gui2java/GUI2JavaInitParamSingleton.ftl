<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "manager", "init", "initList")}

${cd4c.method("protected void ${init}()")}
this.${name} = ${manager}.get${type}();
${tc.includeArgs("umlp.gui2java.GUI2JavaCallMethods", [initList])}